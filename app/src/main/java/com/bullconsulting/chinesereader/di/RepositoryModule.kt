package com.bullconsulting.chinesereader.di

import com.bullconsulting.chinesereader.data.local.EncryptedUserPreferences
import com.bullconsulting.chinesereader.data.remote.qwen.QwenDialogueGenerator
import com.bullconsulting.chinesereader.data.repository.DialogueRepositoryImpl
import com.bullconsulting.chinesereader.data.repository.DictionaryRepositoryImpl
import com.bullconsulting.chinesereader.data.repository.SheetsRepositoryImpl
import com.bullconsulting.chinesereader.data.repository.VocabularyRepositoryImpl
import com.bullconsulting.chinesereader.domain.repository.DialogueGenerator
import com.bullconsulting.chinesereader.domain.repository.DialogueRepository
import com.bullconsulting.chinesereader.domain.repository.DictionaryRepository
import com.bullconsulting.chinesereader.domain.repository.SheetsRepository
import com.bullconsulting.chinesereader.domain.repository.UserPreferences
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Connects each contract to its implementation. Whenever code asks for an
 * interface, Hilt supplies the bound concrete class. This is where
 * "depend on the interface, not the thing" is wired for the whole app.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindVocabularyRepository(
        impl: VocabularyRepositoryImpl,
    ): VocabularyRepository

    @Binds
    abstract fun bindDialogueGenerator(
        impl: QwenDialogueGenerator,
    ): DialogueGenerator

    @Binds
    abstract fun bindUserPreferences(
        impl: EncryptedUserPreferences,
    ): UserPreferences

    @Binds
    abstract fun bindDictionaryRepository(
        impl: DictionaryRepositoryImpl,
    ): DictionaryRepository

    @Binds
    abstract fun bindDialogueRepository(
        impl: DialogueRepositoryImpl,
    ): DialogueRepository

    @Binds
    abstract fun bindSheetsRepository(
        impl: SheetsRepositoryImpl,
    ): SheetsRepository
}
