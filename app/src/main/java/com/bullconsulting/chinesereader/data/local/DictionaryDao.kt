package com.bullconsulting.chinesereader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/** Queries for the bundled dictionary. */
@Dao
interface DictionaryDao {

    @Query("SELECT COUNT(*) FROM dictionary")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(entries: List<DictionaryEntity>)

    @Query("SELECT * FROM dictionary WHERE simplified = :word LIMIT 5")
    suspend fun lookup(word: String): List<DictionaryEntity>

    @Query("DELETE FROM dictionary")
    suspend fun clear()
}
