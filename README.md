# Chinese Reader

An Android app that generates short, natural **Simplified Chinese** dialogues calibrated to
*your* vocabulary. You enter a target word or two; the app asks an LLM (Qwen) to write a
dialogue that uses those words, keeps most of the rest inside words you already know, and
leaves a small remainder of high‑frequency new words — which it **highlights** so you can
learn them in context.

The reading level tracking (what's known / new) is measured **on‑device**, not by the model.

## Features

- **Generate** dialogues from one or more Chinese target words.
- **On‑device analysis** — the text is segmented (Jieba), every word is classified as
  *target / known / unknown*, and the real known‑ratio is shown. Unknown words are highlighted.
- **Vocabulary** — add words manually or **import from a Google Sheet** (one‑way, additive:
  it never deletes your local words). New words auto‑fill pinyin + definition from the dictionary.
- **Offline dictionary** — the full CC‑CEDICT is bundled (~124k entries); tap a highlighted
  word to open it in **Pleco**. Unknown words fall back to per‑character glosses when the whole
  word isn't a dictionary headword.
- **English reverse‑lookup** — don't know the hanzi? Look up an English word and pick the
  Chinese one (ranked by meaning + frequency).
- **History** — save, revisit, and re‑grade generated dialogues against your current vocabulary.
- **Pinyin** shown with tone marks (`nǐ hǎo`), converted for display only.

## How it works

```
target word(s) ─▶ Qwen ─▶ dialogue (plain Chinese)
                             │
                    on-device segmentation (Jieba)
                             │
              classify each word vs. your vocabulary
                             │
        render: targets bold red, unknown words highlighted + tappable
```

## Tech stack

- **Kotlin** · **Jetpack Compose** (UI) · **Material 3**
- **Room** (on‑device SQLite: vocabulary, dictionary, dictionary FTS, saved dialogues)
- **Hilt** (dependency injection)
- **Retrofit** + **kotlinx.serialization** + **OkHttp** (Qwen & Google Sheets HTTP)
- **jieba-analysis** (Chinese word segmentation + word frequencies)
- **Google Identity / play‑services‑auth** (Sheets OAuth)
- **EncryptedSharedPreferences** (the Qwen API key, stored encrypted on device)

## Architecture

Clean, layered — dependencies point inward, so the model, database, or segmenter can be
swapped without touching the rest:

```
ui/        Compose screens + ViewModels
domain/    models, use cases, and repository interfaces (no Android/network deps)
data/      implementations: Room, Qwen client, Sheets client, dictionary seeding
di/        Hilt modules wiring interfaces to implementations
```

See [`docs/PRD.md`](docs/PRD.md) for the full product requirements and design.

## Getting started

> **No credentials are bundled with this project.** Every user supplies their own
> **Qwen API key**, and — for the optional Google Sheets import — their own **Google Cloud
> OAuth client**. Nothing is preconfigured or shared; see the steps below.

### Requirements
- Android Studio (recent) with the Android SDK
- A device or emulator running **Android 8.0 (API 26)** or newer

### Build & run
1. Clone the repo and open it in Android Studio.
2. Let Gradle sync, then **Run** on a device/emulator.

### Configure (in the app → Settings)
- **Qwen API key** — get one from Alibaba Cloud Model Studio; paste it in Settings (stored
  encrypted, on‑device only).
- **Model id** — e.g. `qwen-plus`.
- **Endpoint region** — the DashScope host is a single constant in
  `di/NetworkModule.kt` (`BASE_URL`). Defaults to the **US** region; mainland and
  international alternatives are listed there.
- **Google Sheet URL** — the sheet to import vocabulary from (tab named `known_words`).

### Google Sheets import (optional, one‑time setup)
To enable importing vocabulary from your own Google Sheet:
1. Create a Google Cloud project and enable the **Google Sheets API**.
2. Configure the **OAuth consent screen** (External, Testing) and add yourself as a test user.
3. Add the scope `https://www.googleapis.com/auth/spreadsheets.readonly`.
4. Create an **Android OAuth client** with this app's package name and your debug/release
   signing‑certificate SHA‑1.

Google matches the app by package + SHA‑1, so there is **no client secret** to embed.

## Data & privacy

- **Local‑only.** Vocabulary, dictionary, and saved dialogues live in an on‑device database.
  There is no backend.
- The **Qwen key** is stored with `EncryptedSharedPreferences` and is sent only to Qwen.
- Google Sheets access is **read‑only**.

## Acknowledgements

- Dictionary data: **[CC‑CEDICT](https://www.mdbg.net/chinese/dictionary?page=cc-cedict)**,
  licensed **CC BY‑SA 4.0**.
- Word segmentation & frequencies: **[jieba-analysis](https://github.com/huaban/jieba-analysis)**
  (Apache‑2.0).

## License

This project's source code is licensed under the **MIT License** — see [`LICENSE`](LICENSE).

Bundled third‑party data keeps its own license: **CC‑CEDICT** is CC BY‑SA 4.0 and
**jieba‑analysis** is Apache‑2.0.
