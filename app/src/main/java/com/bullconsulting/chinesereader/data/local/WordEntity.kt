package com.bullconsulting.chinesereader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One row in the "words" table. This is the DATA-layer shape of a word,
 * with database tags. It is kept separate from the domain `Word` on purpose.
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val hanzi: String,
    val pinyin: String?,
    val definition: String?,
    val note: String?,
)
