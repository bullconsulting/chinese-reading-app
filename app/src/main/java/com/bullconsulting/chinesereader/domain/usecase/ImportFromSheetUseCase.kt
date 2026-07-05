package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.ImportResult
import com.bullconsulting.chinesereader.domain.repository.SheetsRepository
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import javax.inject.Inject

/**
 * Imports words from the user's Google Sheet into vocabulary.
 * Additive merge: adds words not already known; never deletes; local wins on overlap.
 * New words are enriched with pinyin/definition via [AddWordUseCase].
 */
class ImportFromSheetUseCase @Inject constructor(
    private val sheets: SheetsRepository,
    private val vocabulary: VocabularyRepository,
    private val addWord: AddWordUseCase,
) {
    suspend operator fun invoke(accessToken: String, sheetUrl: String): Result<ImportResult> =
        runCatching {
            val spreadsheetId = extractSpreadsheetId(sheetUrl)
                ?: throw IllegalArgumentException("That doesn't look like a Google Sheet URL.")

            val words = sheets.fetchWords(accessToken, spreadsheetId, TAB)

            var added = 0
            var skipped = 0
            for (word in words) {
                if (vocabulary.contains(word)) {
                    skipped++
                } else {
                    addWord(word)
                    added++
                }
            }
            ImportResult(added = added, skipped = skipped, total = words.size)
        }

    companion object {
        private const val TAB = "known_words"
    }
}

/** Pulls the spreadsheet id out of a full Google Sheets URL. */
fun extractSpreadsheetId(url: String): String? =
    Regex("/spreadsheets/d/([a-zA-Z0-9-_]+)").find(url)?.groupValues?.get(1)
