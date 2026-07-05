package com.bullconsulting.chinesereader.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bullconsulting.chinesereader.data.local.AppDatabase
import com.bullconsulting.chinesereader.data.local.DialogueDao
import com.bullconsulting.chinesereader.data.local.DictionaryDao
import com.bullconsulting.chinesereader.data.local.DictionaryFtsDao
import com.bullconsulting.chinesereader.data.local.VocabularyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt HOW to build database-related objects, so any part of the app
 * can just ask for a VocabularyDao and receive a ready-made one.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /** v3 -> v4: add the English full-text-search index WITHOUT dropping user data. */
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE VIRTUAL TABLE IF NOT EXISTS `dictionary_fts` USING FTS4(" +
                    "`simplified` TEXT NOT NULL, `pinyin` TEXT NOT NULL, `definition` TEXT NOT NULL)",
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "chinese_reader.db")
            .addMigrations(MIGRATION_3_4)
            // Backstop for other schema changes during development.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideVocabularyDao(database: AppDatabase): VocabularyDao =
        database.vocabularyDao()

    @Provides
    fun provideDictionaryDao(database: AppDatabase): DictionaryDao =
        database.dictionaryDao()

    @Provides
    fun provideDialogueDao(database: AppDatabase): DialogueDao =
        database.dialogueDao()

    @Provides
    fun provideDictionaryFtsDao(database: AppDatabase): DictionaryFtsDao =
        database.dictionaryFtsDao()
}
