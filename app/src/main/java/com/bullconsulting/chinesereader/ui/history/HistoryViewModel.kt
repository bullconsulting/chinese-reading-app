package com.bullconsulting.chinesereader.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue
import com.bullconsulting.chinesereader.domain.model.SavedDialogue
import com.bullconsulting.chinesereader.domain.repository.DialogueRepository
import com.bullconsulting.chinesereader.domain.usecase.AnalyzeDialogueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A saved dialogue re-graded against the CURRENT vocabulary, for the detail view. */
data class DialogueDetail(
    val saved: SavedDialogue,
    val analyzed: AnalyzedDialogue,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DialogueRepository,
    private val analyzeDialogue: AnalyzeDialogueUseCase,
) : ViewModel() {

    val dialogues: StateFlow<List<SavedDialogue>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    var detail by mutableStateOf<DialogueDetail?>(null)
        private set

    fun open(saved: SavedDialogue) {
        viewModelScope.launch {
            // Re-analyse against current vocab so the ratio reflects today's knowledge.
            val analyzed = analyzeDialogue(saved.text, saved.targetWords)
            detail = DialogueDetail(saved, analyzed)
        }
    }

    fun closeDetail() {
        detail = null
    }

    fun delete(id: Long) {
        if (detail?.saved?.id == id) detail = null
        viewModelScope.launch { repository.delete(id) }
    }
}
