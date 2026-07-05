package com.bullconsulting.chinesereader.data.repository

import com.bullconsulting.chinesereader.data.local.DictionaryDao
import com.bullconsulting.chinesereader.data.local.DictionaryFtsDao
import com.bullconsulting.chinesereader.data.local.DictionaryEntity
import com.bullconsulting.chinesereader.data.local.DictionaryFtsEntity
import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import javax.inject.Inject

/** Fulfills the DictionaryRepository contract using the bundled dictionary tables. */
class DictionaryRepositoryImpl @Inject constructor(
    private val dao: DictionaryDao,
    private val ftsDao: DictionaryFtsDao,
) : DictionaryRepository {

    override suspend fun lookup(hanzi: String): List<DictionaryEntry> =
        dao.lookup(hanzi).map { it.toEntry() }

    override suspend fun searchByEnglish(query: String, limit: Int): List<DictionaryEntry> {
        val match = buildFtsMatch(query) ?: return emptyList()
        return ftsDao.search(match, limit).map { it.toEntry() }
    }

    /** Turn free English text into a safe FTS MATCH expression (AND of word tokens). */
    private fun buildFtsMatch(raw: String): String? {
        val terms = raw.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split(" ")
            .filter { it.isNotBlank() }
        return if (terms.isEmpty()) null else terms.joinToString(" ")
    }
}

private fun DictionaryEntity.toEntry() = DictionaryEntry(
    simplified = simplified,
    pinyin = pinyin,
    definitions = definition.split("; ").filter { it.isNotBlank() },
)

private fun DictionaryFtsEntity.toEntry() = DictionaryEntry(
    simplified = simplified,
    pinyin = pinyin,
    definitions = definition.split("; ").filter { it.isNotBlank() },
)
