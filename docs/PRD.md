# Product Requirements Document — Chinese Reading App (v2)

**Status:** Draft v1 · **Owner:** bullconsulting · **Date:** 2026-07-03
**Guiding principles:** Separation of Concerns · Easy To Change (ETC) · Security · Readability

---

## 1. Vision

A personal Android app that generates short, natural dialogues in Simplified
Chinese calibrated to the learner's own vocabulary. The learner enters one or
more target words/characters; the app produces a dialogue that (a) uses those
targets naturally, (b) keeps ~90% of the remaining words inside the learner's
*known* vocabulary, and (c) lets the remaining ~10% be **high-frequency unknown
words**, which are visually highlighted. The result is comprehensible input at
the "i+1" difficulty sweet spot, on demand.

---

## 2. Goals & Non-Goals (v1)

### Goals
- Generate a short dialogue from user-supplied target word(s)/character(s).
- Maintain a local **known-vocabulary** database (manual add + list import).
- Analyze each generated dialogue on-device and classify every word as
  **target / known / unknown**; highlight unknown (and optionally target) words.
- Tap any word for **pinyin + definition** (bundled offline dictionary).
- Save / revisit generated dialogues locally.
- Keep the Qwen API key secure (Android Keystore), never bundled in the APK.

### Non-Goals (explicitly out of scope for v1)
- No backend server, user accounts, or cloud sync.
- No cross-device sync (local-only; export/import is the backup mechanism).
- No HSK auto-seeding or automatic vocab growth from reading (deferred).
- No audio/TTS, SRS/flashcards, or grammar explanations (deferred).
- No Play Store release, analytics, or telemetry.

---

## 3. User & Usage

Single primary user (the owner) plus a handful of trusted testers via sideloaded
APK. Each user supplies their **own** Qwen API key. Assumes an intermediate
learner who already has a vocabulary list to import.

**Core loop:**
1. User seeds/imports their known-vocabulary list (one-time-ish).
2. User types target word(s) → taps *Generate*.
3. App calls Qwen with a calibrated prompt → receives a dialogue.
4. App segments & classifies the dialogue on-device → renders with highlights.
5. User reads, taps words for meaning, optionally adds unknown words to vocab,
   optionally saves the dialogue.

---

## 4. Key Decisions (settled)

| Decision | Choice | Rationale |
|---|---|---|
| API key model | **User brings own key**, stored in Android Keystore | Personal use; no backend to build/host; zero shared-secret risk |
| Data storage | **Local-only** SQLite via Room | Offline, private, fast; no accounts |
| Vocab seeding | **Manual add + list import** (CSV/TXT) | User controls; import covers HSK lists too |
| v1 target | **Personal / small group**, sideloaded APK | Tight scope, fastest to working MVP |
| Platform | **Native Android — Kotlin + Jetpack Compose** | Best Keystore/security story, no JS bridge, clean layering |

---

## 5. Functional Requirements

### 5.1 Vocabulary management
- **FR-V1** Add a single word manually (hanzi; pinyin/definition auto-filled from bundled dictionary when available).
- **FR-V2** Import a list from a `.txt`/`.csv` file (one word per line, or `word,note`), with a preview + dedupe before commit.
- **FR-V3** View, search, edit, and delete vocabulary entries.
- **FR-V4** Export the vocabulary list (for backup) to a file.

### 5.2 Dialogue generation
- **FR-G1** Enter one or more target words/characters (free text).
- **FR-G2** Configure generation: approximate length, number of speakers (2 default), and known-ratio target (default 90%).
- **FR-G3** Call Qwen and display the resulting dialogue; show a clear loading + error state.
- **FR-G4** Cancel an in-flight generation.

### 5.3 On-device analysis & rendering (core differentiator)
- **FR-A1** Segment the returned Chinese text into words on-device.
- **FR-A2** Classify each word as **target**, **known**, or **unknown** against the local vocab DB.
- **FR-A3** Highlight unknown words (distinct style) and optionally target words.
- **FR-A4** Compute and display the actual known-ratio (e.g. "known 92% · unknown 8%").
- **FR-A5** If known-ratio falls below a threshold, offer **Refine** — regenerate with a feedback prompt listing the offending unknown words to avoid.

### 5.4 Reading aids
- **FR-R1** Tap a word → bottom sheet with hanzi, pinyin, definition, and its classification.
- **FR-R2** From that sheet, **add the word to known vocabulary** (moves it known on re-render).

### 5.5 History
- **FR-H1** Save a generated dialogue (with its target words + analysis snapshot).
- **FR-H2** Browse, reopen, and delete saved dialogues.

### 5.6 Settings
- **FR-S1** Enter / update / clear the Qwen API key (stored encrypted; never logged).
- **FR-S2** Choose Qwen model + set defaults (length, ratio, highlight target on/off).

---

## 6. The Generation → Analysis Pipeline (core design)

Separation of concerns is deliberate here: **the LLM generates natural
language; the app owns all measurement and highlighting.** We never trust the
model to self-report ratios or tag words.

```
[Target words] + [Vocab context] ──► Qwen ──► raw dialogue (plain Chinese)
                                                   │
                          on-device segmentation (Jieba port)
                                                   │
                    classify each token vs. vocab DB + target set
                                                   │
              ┌──────── known-ratio ≥ threshold? ────────┐
              │ yes                                        │ no
        render + highlight                          offer Refine:
                                              regenerate telling model to
                                              avoid the specific unknown words
```

**Why measure locally:** the 90/10 target cannot be guaranteed by a prompt.
On-device analysis gives the *actual* ratio deterministically and testably, and
drives the optional refine loop — keeping token cost down (we don't ship the
whole vocab to the model; we send targets + a level hint, then correct with a
short "avoid these" list only if needed).

---

## 7. Data Model (Room)

- **`vocabulary`** — `id`, `hanzi` (unique), `pinyin?`, `definition?`, `note?`, `added_at`
- **`dialogue`** — `id`, `target_words`, `content`, `known_ratio`, `model`, `created_at`
- **`dialogue_token`** *(analysis snapshot)* — `id`, `dialogue_id (fk)`, `surface`, `start_index`, `classification` (target|known|unknown), `pinyin?`
- **Bundled read-only assets** (packaged in the APK, not user data):
  - **Dictionary** — CC-CEDICT (CC BY-SA), preprocessed into an indexed SQLite/Room table for tap-to-define.
  - **Frequency list** (optional, nice-to-have) — to sanity-check that unknowns are genuinely high-frequency and to sort them.

---

## 8. Prompt Design (draft)

System-style instruction sent with each request:

> You write short, natural dialogues in Simplified Chinese for a language
> learner. Requirements:
> 1. Use the TARGET WORDS as naturally and frequently as the topic allows: `{targets}`.
> 2. Keep vocabulary simple and high-frequency. The learner knows roughly
>    `{vocab_size}` common words (about HSK `{level_hint}`).
> 3. Aim for ~2 speakers and about `{length}` characters.
> 4. Output ONLY the dialogue lines as `Speaker: 中文`. No pinyin, no
>    translation, no explanation.

Refine turn (only if ratio below threshold) appends:

> The previous version used these words the learner does not know — avoid them
> and prefer simpler synonyms: `{unknown_words}`.

Notes: we do **not** dump the full vocabulary into the prompt (token cost); we
lean on the level hint + local measurement + targeted "avoid" feedback.

---

## 9. Architecture (separation of concerns / ETC)

Clean layered architecture, native Kotlin:

```
presentation/   Compose screens + ViewModels (state, no business logic)
    │ depends on ▼
domain/         Pure Kotlin use-cases & models — no Android/network deps
    │           GenerateDialogue · AnalyzeText · ManageVocabulary · LookupWord
    │ depends on interfaces ▼
data/           Implementations behind interfaces:
      LlmClient (interface) ──► QwenClient          ← swap model / add backend later
      Segmenter (interface) ──► JiebaSegmenter
      VocabularyRepository  ──► Room
      DialogueRepository    ──► Room
      DictionaryRepository  ──► bundled CC-CEDICT (read-only)
      SecretStore (interface)─► AndroidKeystoreStore ← API key
```

- **ETC lever:** `LlmClient` and `Segmenter` are interfaces. Swapping Qwen for
  another model — or introducing a backend proxy in a later version — touches
  only one `data/` implementation, never `domain/` or `presentation/`.
- **DI:** Hilt for wiring and testability.
- **Testability:** `domain/` (classification, ratio math, refine decision) is
  pure and unit-tested with no device/network.

**Proposed stack:** Kotlin · Jetpack Compose · Room · Hilt · Retrofit/OkHttp
(Qwen HTTP) · `jieba-analysis` (Java Jieba port) · Kotlin Coroutines/Flow.

---

## 10. Security

- **Secret handling:** Qwen key entered by the user, stored via Android Keystore
  (`EncryptedSharedPreferences` / Keystore-backed). Never hard-coded, never in
  version control, never written to logs or crash reports.
- **Network:** HTTPS only to the Qwen endpoint; certificate defaults; no
  cleartext traffic (`android:usesCleartextTraffic="false"`).
- **Least data:** No analytics/telemetry; all user data stays on device.
- **Input handling:** treat model output as untrusted text — render as text,
  never as HTML/markup that could inject; guard segmentation against malformed
  output.
- **Backup:** exclude the encrypted key from Android auto-backup.

---

## 11. Non-Functional Requirements

- **Offline-first** for everything except generation (which needs the network).
- **Performance:** segmentation + classification of a short dialogue < 200 ms;
  dictionary lookup < 50 ms.
- **Resilience:** clear, actionable states for no-network, bad/expired key,
  rate-limit, and malformed model output.
- **Readability:** consistent Kotlin style; ktlint/detekt; small, named
  use-cases over god-classes.

---

## 12. Risks & Open Questions

1. **Small vocab breaks the 90% target.** If the imported known-vocab is tiny,
   a "90% known" dialogue is impossible. Mitigation: show the *actual* ratio
   honestly; recommend importing a substantial list (e.g. an HSK level) first.
2. **Segmentation accuracy.** Jieba is good but not perfect on Simplified;
   mis-segmentation can misclassify words. Mitigation: keep segmentation behind
   an interface so it can be tuned/replaced; allow user to correct.
3. **"High-frequency" unknowns not guaranteed.** The prompt requests it; the
   optional bundled frequency list lets us verify/sort. Accept best-effort in v1.
4. **Token cost of context.** Resolved by not sending full vocab; using level
   hint + refine loop. Monitor real usage.
5. **Dictionary licensing.** CC-CEDICT is CC BY-SA — include attribution in an
   About screen.

---

## 13. Phased Plan / Milestones

**M0 — Foundations**
Project scaffold, layered module structure, Hilt DI, Keystore `SecretStore`,
Settings screen to store/clear the Qwen key. *Exit:* key persists securely.

**M1 — Vocabulary**
Room schema, add/edit/delete/search, list import (TXT/CSV) with preview+dedupe,
export. *Exit:* can import an HSK list and browse it.

**M2 — Dictionary**
Bundle & index CC-CEDICT; `LookupWord` use-case; tap-to-define bottom sheet.
*Exit:* tapping any hanzi shows pinyin + definition offline.

**M3 — Generation**
`LlmClient`/`QwenClient`, prompt builder, generate screen with target input +
options, loading/error/cancel states. *Exit:* real dialogues come back.

**M4 — Analysis & Highlighting (the core)**
`Segmenter` (Jieba), `AnalyzeText` classification, known-ratio, highlighted
render, Refine loop, "add to vocab" from the reader. *Exit:* dialogues render
with correct highlights and honest ratio.

**M5 — History & Polish**
Save/browse/delete dialogues, empty/error-state polish, About/attribution,
ktlint/detekt, unit tests on `domain/`. *Exit:* usable daily-driver MVP.

---

## 14. Success Criteria (v1)

- From a seeded vocab list, generating a dialogue for a target word produces a
  readable dialogue in which unknown words are correctly highlighted and the
  displayed known-ratio matches a manual count.
- The Qwen key is never present anywhere in the repo or logs.
- All of the above works with the network off *except* the generation call.
