package com.bullconsulting.chinesereader.data.repository

import com.bullconsulting.chinesereader.data.local.VocabularyDao
import com.bullconsulting.chinesereader.data.local.toDomain
import com.bullconsulting.chinesereader.data.local.toEntity
import com.bullconsulting.chinesereader.domain.model.Word
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fulfills the VocabularyRepository contract using the Room database.
 * It talks to the DAO and translates between WordEntity and Word.
 */
class VocabularyRepositoryImpl @Inject constructor(
    private val dao: VocabularyDao,
) : VocabularyRepository {

    override fun observeAll(): Flow<List<Word>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun getAll(): List<Word> =
        dao.getAll().map { it.toDomain() }

    override suspend fun add(word: Word) = dao.insert(word.toEntity())

    override suspend fun delete(hanzi: String) = dao.deleteByHanzi(hanzi)

    override suspend fun contains(hanzi: String): Boolean = dao.exists(hanzi)
}
