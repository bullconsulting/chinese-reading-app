package com.bullconsulting.chinesereader.data.local

import androidx.room.Entity
import androidx.room.Fts4

/**
 * Full-text-search index over dictionary definitions, so we can search the
 * dictionary by ENGLISH gloss (e.g. "friend" -> 朋友). Separate from the main
 * `dictionary` table, which stays a plain table for fast exact hanzi lookups.
 */
@Fts4
@Entity(tableName = "dictionary_fts")
data class DictionaryFtsEntity(
    val simplified: String,
    val pinyin: String,
    val definition: String,
)
