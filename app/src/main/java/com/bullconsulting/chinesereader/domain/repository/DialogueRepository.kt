package com.bullconsulting.chinesereader.domain.repository

import com.bullconsulting.chinesereader.domain.model.SavedDialogue
import kotlinx.coroutines.flow.Flow

/** Contract for saving and reading generated dialogues (history). */
interface DialogueRepository {
    fun observeAll(): Flow<List<SavedDialogue>>
    suspend fun save(targetWords: List<String>, text: String, knownRatio: Float)
    suspend fun delete(id: Long)
}
