package com.bullconsulting.chinesereader.domain.model

/** A generated dialogue: the raw Chinese text plus the target words it was built around. */
data class Dialogue(
    val text: String,
    val targetWords: List<String>,
)
