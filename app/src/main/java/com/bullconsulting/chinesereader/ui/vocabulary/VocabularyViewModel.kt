package com.bullconsulting.chinesereader.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bullconsulting.chinesereader.domain.model.Word
import com.bullconsulting.chinesereader.domain.repository.VocabularyRepository
import com.bullconsulting.chinesereader.domain.usecase.AddWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the vocabulary screen's state and handles its actions.
 * It knows only the repository CONTRACT and a use case, never the database.
 */
@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val repository: VocabularyRepository,
    private val addWordUseCase: AddWordUseCase,
) : ViewModel() {

    /** Live list of words the screen observes; refreshes automatically. */
    val words: StateFlow<List<Word>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun addWord(hanzi: String) {
        viewModelScope.launch { addWordUseCase(hanzi) }
    }

    fun deleteWord(hanzi: String) {
        viewModelScope.launch { repository.delete(hanzi) }
    }
}
