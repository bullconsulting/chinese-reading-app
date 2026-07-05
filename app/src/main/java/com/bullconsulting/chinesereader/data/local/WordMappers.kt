package com.bullconsulting.chinesereader.data.local

import com.bullconsulting.chinesereader.domain.model.Word

/**
 * Translators between the two word shapes:
 *  - `Word`       : clean domain model (no database details)
 *  - `WordEntity` : database row (carries Room tags)
 * Keeping them separate is what protects the domain from storage concerns.
 */
fun WordEntity.toDomain(): Word = Word(
    hanzi = hanzi,
    pinyin = pinyin,
    definition = definition,
    note = note,
)

fun Word.toEntity(): WordEntity = WordEntity(
    hanzi = hanzi,
    pinyin = pinyin,
    definition = definition,
    note = note,
)
