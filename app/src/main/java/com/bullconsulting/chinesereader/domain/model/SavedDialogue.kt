package com.bullconsulting.chinesereader.domain.model

/** A dialogue the user saved, with the words it targeted and when it was made. */
data class SavedDialogue(
    val id: Long,
    val targetWords: List<String>,
    val text: String,
    val knownRatio: Float,
    val createdAt: Long,
)
