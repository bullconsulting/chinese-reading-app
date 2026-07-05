package com.bullconsulting.chinesereader.data.repository

import com.bullconsulting.chinesereader.data.local.DialogueDao
import com.bullconsulting.chinesereader.data.local.DialogueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** In-memory stand-in for the DialogueDao. */
private class FakeDialogueDao : DialogueDao {
    val rows = mutableListOf<DialogueEntity>()
    override fun observeAll(): Flow<List<DialogueEntity>> = flowOf(rows.toList())
    override suspend fun insert(dialogue: DialogueEntity) {
        rows.add(dialogue)
    }
    override suspend fun deleteById(id: Long) {
        rows.removeAll { it.id == id }
    }
}

class DialogueRepositoryTest {

    @Test
    fun save_joinsTargetWords_andObserveSplitsThemBack() = runTest {
        val dao = FakeDialogueDao()
        val repository = DialogueRepositoryImpl(dao)

        repository.save(targetWords = listOf("喜欢", "旅行"), text = "A: 你好", knownRatio = 0.9f)

        assertEquals("喜欢 旅行", dao.rows.first().targetWords)

        val observed = repository.observeAll().first().first()
        assertEquals(listOf("喜欢", "旅行"), observed.targetWords)
        assertEquals(0.9f, observed.knownRatio, 0.001f)
    }
}
