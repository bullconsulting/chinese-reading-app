package com.bullconsulting.chinesereader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One saved dialogue row. `targetWords` is stored space-joined. */
@Entity(tableName = "dialogues")
data class DialogueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetWords: String,
    val text: String,
    val knownRatio: Float,
    val createdAt: Long,
)
