package com.bullconsulting.chinesereader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room database. Lists its tables (entities) and version.
 * v2 added `dictionary`; v3 added `dialogues`; v4 added the `dictionary_fts`
 * English full-text-search index.
 */
@Database(
    entities = [
        WordEntity::class,
        DictionaryEntity::class,
        DialogueEntity::class,
        DictionaryFtsEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun dialogueDao(): DialogueDao
    abstract fun dictionaryFtsDao(): DictionaryFtsDao
}
