package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue
import com.bullconsulting.chinesereader.domain.model.AnalyzedToken
import com.bullconsulting.chinesereader.domain.model.TokenType
import com.bullconsulting.chinesereader.domain.repository.Segmenter
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import com.bullconsulting.chinesereader.domain.util.isHan
import javax.inject.Inject

/**
 * Splits a dialogue into words and tags each one as target / known / unknown.
 * This is the app's core differentiator: measurement happens here, on-device,
 * never trusting the language model to grade itself.
 */
class AnalyzeDialogueUseCase @Inject constructor(
    private val segmenter: Segmenter,
    private val vocabulary: VocabularyRepository,
) {
    suspend operator fun invoke(
        text: String,
        targetWords: List<String>,
    ): AnalyzedDialogue {
        val known = vocabulary.getAll().map { it.hanzi }.toSet()
        val targets = targetWords.toSet()

        val tokens = segmenter.segment(text).map { surface ->
            val type = when {
                !surface.any { it.isHan() } -> TokenType.OTHER
                surface in targets -> TokenType.TARGET
                surface in known -> TokenType.KNOWN
                else -> TokenType.UNKNOWN
            }
            AnalyzedToken(surface = surface, type = type)
        }

        val chineseTokens = tokens.filter { it.type != TokenType.OTHER }
        val knownCount = chineseTokens.count {
            it.type == TokenType.KNOWN || it.type == TokenType.TARGET
        }
        val knownRatio =
            if (chineseTokens.isEmpty()) 0f else knownCount.toFloat() / chineseTokens.size

        val unknownWords = tokens
            .filter { it.type == TokenType.UNKNOWN }
            .map { it.surface }
            .distinct()

        return AnalyzedDialogue(
            tokens = tokens,
            knownRatio = knownRatio,
            unknownWords = unknownWords,
        )
    }
}
