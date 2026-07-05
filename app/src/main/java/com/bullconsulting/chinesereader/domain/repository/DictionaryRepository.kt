package com.bullconsulting.chinesereader.domain.repository

import com.bullconsulting.chinesereader.domain.model.DictionaryEntry

/** Contract for looking up the meaning + pinyin of a Chinese word. */
interface DictionaryRepository {
    /** Exact lookup by Chinese word. */
    suspend fun lookup(hanzi: String): List<DictionaryEntry>

    /**
     * Reverse lookup: find Chinese words whose definition matches an English term.
     * The limit is a generous candidate pool that ranking then narrows — it must be
     * high enough that common words aren't cut off before ranking (FTS returns rows
     * in table order, not relevance order).
     */
    suspend fun searchByEnglish(query: String, limit: Int = 400): List<DictionaryEntry>
}
