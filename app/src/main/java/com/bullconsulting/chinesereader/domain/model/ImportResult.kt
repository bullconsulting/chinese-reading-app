package com.bullconsulting.chinesereader.domain.model

/** Outcome of a Google Sheet import: how many words were added vs already known. */
data class ImportResult(
    val added: Int,
    val skipped: Int,
    val total: Int,
)
