package com.bullconsulting.chinesereader.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One dictionary entry row. Indexed by `simplified` so lookups are fast even
 * across ~120k rows.
 */
@Entity(tableName = "dictionary", indices = [Index(value = ["simplified"])])
data class DictionaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val simplified: String,
    val traditional: String,
    val pinyin: String,
    val definition: String,
)
