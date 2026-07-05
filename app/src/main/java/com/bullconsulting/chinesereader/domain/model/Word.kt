package com.bullconsulting.chinesereader.domain.model

/**
 * A single vocabulary word the user knows.
 *
 * Pure data — no database or UI details here. `hanzi` is the identity
 * (the Chinese characters); the rest are optional helpers.
 */
data class Word(
    val hanzi: String,
    val pinyin: String? = null,
    val definition: String? = null,
    val note: String? = null,
)
