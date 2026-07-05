package com.bullconsulting.chinesereader.data.repository

import com.bullconsulting.chinesereader.data.remote.sheets.SheetsApi
import com.bullconsulting.chinesereader.domain.repository.SheetsRepository
import com.bullconsulting.chinesereader.domain.util.containsHan
import javax.inject.Inject

/**
 * Reads a sheet tab and returns the Chinese words in it. Any cell containing Han
 * characters counts as a word; headers, blanks and non-Chinese cells are skipped.
 */
class SheetsRepositoryImpl @Inject constructor(
    private val api: SheetsApi,
) : SheetsRepository {

    override suspend fun fetchWords(
        accessToken: String,
        spreadsheetId: String,
        tab: String,
    ): List<String> {
        val response = api.getValues(
            authorization = "Bearer $accessToken",
            spreadsheetId = spreadsheetId,
            range = tab,
        )
        return response.values
            .flatten()
            .map { it.trim() }
            .filter { it.containsHan() }
            .distinct()
    }
}
