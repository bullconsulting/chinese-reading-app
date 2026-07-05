package com.bullconsulting.chinesereader.data.repository

import com.bullconsulting.chinesereader.data.local.DialogueDao
import com.bullconsulting.chinesereader.data.local.DialogueEntity
import com.bullconsulting.chinesereader.domain.model.SavedDialogue
import com.bullconsulting.chinesereader.domain.repository.DialogueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Fulfills the DialogueRepository contract using Room. */
class DialogueRepositoryImpl @Inject constructor(
    private val dao: DialogueDao,
) : DialogueRepository {

    override fun observeAll(): Flow<List<SavedDialogue>> =
        dao.observeAll().map { rows ->
            rows.map { row ->
                SavedDialogue(
                    id = row.id,
                    targetWords = row.targetWords.split(" ").filter { it.isNotBlank() },
                    text = row.text,
                    knownRatio = row.knownRatio,
                    createdAt = row.createdAt,
                )
            }
        }

    override suspend fun save(targetWords: List<String>, text: String, knownRatio: Float) =
        dao.insert(
            DialogueEntity(
                targetWords = targetWords.joinToString(" "),
                text = text,
                knownRatio = knownRatio,
                createdAt = System.currentTimeMillis(),
            ),
        )

    override suspend fun delete(id: Long) = dao.deleteById(id)
}
