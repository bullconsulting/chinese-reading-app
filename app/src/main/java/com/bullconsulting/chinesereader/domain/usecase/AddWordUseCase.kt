package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.Word
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import javax.inject.Inject

/**
 * Adds a word to vocabulary, auto-filling pinyin + definition from the dictionary.
 * This is the "build it, then store it" split we discussed: the dictionary builds
 * a rich Word; the vocabulary repository stores it.
 */
class AddWordUseCase @Inject constructor(
    private val vocabulary: VocabularyRepository,
    private val dictionary: DictionaryRepository,
) {
    suspend operator fun invoke(hanzi: String) {
        val clean = hanzi.trim()
        if (clean.isEmpty()) return
        val entry = dictionary.lookup(clean).firstOrNull()
        vocabulary.add(
            Word(
                hanzi = clean,
                pinyin = entry?.pinyin,
                definition = entry?.definitions?.joinToString("; ")?.take(200),
            ),
        )
    }
}
