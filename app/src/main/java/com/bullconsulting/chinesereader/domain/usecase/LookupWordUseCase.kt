package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import com.bullconsulting.chinesereader.domain.util.isHan
import javax.inject.Inject

/**
 * Looks up the best dictionary entry for a word.
 *
 * If the whole word isn't a dictionary headword (names like 小明, colloquialisms
 * like 太棒了, or segmentation artifacts), it falls back to glossing each character
 * so the reader still gets pinyin and a rough meaning instead of a blank.
 */
class LookupWordUseCase @Inject constructor(
    private val dictionary: DictionaryRepository,
) {
    suspend operator fun invoke(hanzi: String): DictionaryEntry? {
        dictionary.lookup(hanzi).firstOrNull()?.let { return it }
        return composeFromCharacters(hanzi)
    }

    private suspend fun composeFromCharacters(word: String): DictionaryEntry? {
        val chars = word.filter { it.isHan() }.map { it.toString() }
        if (chars.isEmpty()) return null

        val parts = chars.map { ch ->
            val entry = dictionary.lookup(ch).firstOrNull()
            CharPart(ch, entry?.pinyin, entry?.definitions?.firstOrNull())
        }
        if (parts.none { it.pinyin != null || it.gloss != null }) return null

        val pinyin = parts.mapNotNull { it.pinyin }.joinToString(" ")
        val gloss = parts.joinToString(" · ") { part ->
            if (part.gloss != null) "${part.char} ${part.gloss}" else part.char
        }
        return DictionaryEntry(simplified = word, pinyin = pinyin, definitions = listOf(gloss))
    }

    private data class CharPart(val char: String, val pinyin: String?, val gloss: String?)
}
