package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.data.segmentation.JiebaSegmenterImpl
import com.bullconsulting.chinesereader.domain.model.TokenType
import com.bullconsulting.chinesereader.domain.model.Word
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** A stand-in repository so we can test analysis without a real database. */
private class FakeVocabularyRepository(private val words: List<Word>) : VocabularyRepository {
    override fun observeAll(): Flow<List<Word>> = flowOf(words)
    override suspend fun getAll(): List<Word> = words
    override suspend fun add(word: Word) = Unit
    override suspend fun delete(hanzi: String) = Unit
    override suspend fun contains(hanzi: String): Boolean = words.any { it.hanzi == hanzi }
}

class AnalyzeDialogueUseCaseTest {

    private fun useCaseWithKnown(vararg known: String) = AnalyzeDialogueUseCase(
        segmenter = JiebaSegmenterImpl(),
        vocabulary = FakeVocabularyRepository(known.map { Word(hanzi = it) }),
    )

    @Test
    fun allWordsKnownOrTarget_givesFullRatioAndNoUnknowns() = runTest {
        val useCase = useCaseWithKnown("我", "喜欢")
        val result = useCase(text = "我喜欢旅行", targetWords = listOf("旅行"))

        assertTrue("expected no unknown words", result.unknownWords.isEmpty())
        assertEquals(1f, result.knownRatio, 0.001f)
        assertTrue(result.tokens.any { it.surface == "旅行" && it.type == TokenType.TARGET })
    }

    @Test
    fun unknownWord_isFlaggedAndLowersRatio() = runTest {
        val useCase = useCaseWithKnown("我", "喜欢")
        val result = useCase(text = "我喜欢学习", targetWords = emptyList())

        assertTrue("expected 学习 to be unknown", result.unknownWords.contains("学习"))
        assertTrue("ratio should be below 1", result.knownRatio < 1f)
    }
}
