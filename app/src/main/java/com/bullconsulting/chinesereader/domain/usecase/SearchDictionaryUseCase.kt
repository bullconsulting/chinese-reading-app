package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import com.bullconsulting.chinesereader.domain.repository.WordFrequency
import javax.inject.Inject

/**
 * Finds Chinese words for an English term, ranked so the most likely candidates
 * come first. Ranking = how central the term is to the word's meaning (sense
 * tier), then how common the word is (frequency). The user still picks.
 *
 * Sense tier deliberately favours words whose PRIMARY meaning is the term, so a
 * common word that merely lists the term as a minor/archaic sense (e.g. 故 for
 * "friend") does not outrank a dedicated word (朋友).
 */
class SearchDictionaryUseCase @Inject constructor(
    private val dictionary: DictionaryRepository,
    private val frequency: WordFrequency,
) {
    suspend operator fun invoke(englishTerm: String): List<DictionaryEntry> {
        val term = englishTerm.trim()
        if (term.length < 2) return emptyList()

        val query = term.lowercase()
        return dictionary.searchByEnglish(term)
            .distinctBy { it.simplified }
            .sortedWith(
                compareByDescending<DictionaryEntry> { senseTier(it, query) }
                    .thenByDescending { frequency.weight(it.simplified) },
            )
            .take(20)
    }

    private fun senseTier(entry: DictionaryEntry, query: String): Int {
        val senses = realSenses(entry)
        val primary = senses.firstOrNull()
        return when {
            primary == query || primary == "to $query" -> 4 // the word's main meaning
            senses.any { it == query || it == "to $query" } -> 3 // exact, but a secondary sense
            senses.any {
                it.startsWith("$query ") || it.startsWith("to $query ") ||
                    it.endsWith(" $query") || it.contains(" $query ")
            } -> 2 // the term appears as a whole word
            senses.any { it.contains(query) } -> 1 // substring only
            else -> 0
        }
    }

    /** Drop CC-CEDICT annotations that aren't real meanings (classifiers, cross-refs). */
    private fun realSenses(entry: DictionaryEntry): List<String> =
        entry.definitions
            .map { it.trim().lowercase() }
            .filter {
                it.isNotEmpty() &&
                    !it.startsWith("cl:") &&
                    !it.startsWith("see ") &&
                    !it.startsWith("variant of") &&
                    !it.startsWith("old variant of")
            }
}
