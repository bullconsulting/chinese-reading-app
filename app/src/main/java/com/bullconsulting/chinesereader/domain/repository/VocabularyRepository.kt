package com.bullconsulting.chinesereader.domain.repository

import com.bullconsulting.chinesereader.domain.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * The contract for storing and reading the user's known vocabulary.
 *
 * The domain layer declares WHAT it needs (these actions). The data layer
 * decides HOW (a database, a file, anything). Code depends on this contract,
 * never on the concrete storage — so we can swap storage without touching callers.
 */
interface VocabularyRepository {
    /** A live, auto-updating stream of all words (for the screen to watch). */
    fun observeAll(): Flow<List<Word>>

    /** A one-shot snapshot of all words (for jobs like text analysis). */
    suspend fun getAll(): List<Word>

    suspend fun add(word: Word)
    suspend fun delete(hanzi: String)
    suspend fun contains(hanzi: String): Boolean
}
