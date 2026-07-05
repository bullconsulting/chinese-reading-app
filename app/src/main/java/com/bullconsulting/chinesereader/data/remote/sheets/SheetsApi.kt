package com.bullconsulting.chinesereader.data.remote.sheets

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/** A slice of cell values returned by the Sheets API. */
@Serializable
data class ValueRange(
    val values: List<List<String>> = emptyList(),
)

/** Read-only access to Google Sheets cell values. */
interface SheetsApi {
    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getValues(
        @Header("Authorization") authorization: String,
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
    ): ValueRange
}
