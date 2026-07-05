package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeDictionary(private val entries: Map<String, DictionaryEntry>) : DictionaryRepository {
    override suspend fun lookup(hanzi: String): List<DictionaryEntry> =
        entries[hanzi]?.let { listOf(it) } ?: emptyList()
    override suspend fun searchByEnglish(query: String, limit: Int): List<DictionaryEntry> = emptyList()
}

class LookupWordUseCaseTest {

    @Test
    fun prefersWholeWordWhenPresent() = runTest {
        val dict = FakeDictionary(
            mapOf("朋友" to DictionaryEntry("朋友", "peng2 you5", listOf("friend"))),
        )
        val result = LookupWordUseCase(dict)("朋友")
        assertEquals("peng2 you5", result?.pinyin)
        assertEquals("friend", result?.definitions?.first())
    }

    @Test
    fun fallsBackToPerCharacterGlossWhenWordMissing() = runTest {
        val dict = FakeDictionary(
            mapOf(
                "真" to DictionaryEntry("真", "zhen1", listOf("real", "genuine")),
                "好" to DictionaryEntry("好", "hao3", listOf("good", "nice")),
            ),
        )
        // 真好 is not a headword; should compose from 真 + 好.
        val result = LookupWordUseCase(dict)("真好")
        assertEquals("zhen1 hao3", result?.pinyin)
        assertEquals("真 real · 好 good", result?.definitions?.first())
    }
}
