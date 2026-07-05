package com.bullconsulting.chinesereader.domain.repository

/** Contract for reading a list of Chinese words from a Google Sheet tab. */
interface SheetsRepository {
    suspend fun fetchWords(accessToken: String, spreadsheetId: String, tab: String): List<String>
}
