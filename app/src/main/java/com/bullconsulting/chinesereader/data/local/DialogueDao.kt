package com.bullconsulting.chinesereader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Queries for saved dialogues (the history). */
@Dao
interface DialogueDao {

    @Query("SELECT * FROM dialogues ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DialogueEntity>>

    @Insert
    suspend fun insert(dialogue: DialogueEntity)

    @Query("DELETE FROM dialogues WHERE id = :id")
    suspend fun deleteById(id: Long)
}
