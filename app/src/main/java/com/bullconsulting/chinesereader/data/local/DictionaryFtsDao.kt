package com.bullconsulting.chinesereader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/** Queries for the English full-text-search index. */
@Dao
interface DictionaryFtsDao {

    @Query("SELECT COUNT(*) FROM dictionary_fts")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(rows: List<DictionaryFtsEntity>)

    @Query("DELETE FROM dictionary_fts")
    suspend fun clear()

    @Query("SELECT * FROM dictionary_fts WHERE dictionary_fts MATCH :query LIMIT :limit")
    suspend fun search(query: String, limit: Int): List<DictionaryFtsEntity>
}
