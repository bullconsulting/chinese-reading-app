package com.bullconsulting.chinesereader.domain.model

/** A dictionary result for a word: its pinyin and one or more meanings. */
data class DictionaryEntry(
    val simplified: String,
    val pinyin: String,
    val definitions: List<String>,
)
