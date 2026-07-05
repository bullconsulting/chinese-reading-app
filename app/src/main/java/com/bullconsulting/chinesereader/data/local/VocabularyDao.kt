package com.bullconsulting.chinesereader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO = Data Access Object: the set of database queries for the "words" table.
 * We write short instructions; Room generates the real SQLite code.
 */
@Dao
interface VocabularyDao {

    @Query("SELECT * FROM words ORDER BY hanzi")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY hanzi")
    suspend fun getAll(): List<WordEntity>

    // IGNORE: if the word already exists, keep the existing row (local wins).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: WordEntity)

    @Query("DELETE FROM words WHERE hanzi = :hanzi")
    suspend fun deleteByHanzi(hanzi: String)

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE hanzi = :hanzi)")
    suspend fun exists(hanzi: String): Boolean
}
