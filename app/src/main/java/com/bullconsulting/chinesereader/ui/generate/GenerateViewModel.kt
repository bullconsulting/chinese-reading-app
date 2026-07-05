package com.bullconsulting.chinesereader.ui.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue
import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.domain.model.GenerationRequest
import com.bullconsulting.chinesereader.domain.repository.DialogueRepository
import com.bullconsulting.chinesereader.domain.usecase.AddWordUseCase
import com.bullconsulting.chinesereader.domain.usecase.AnalyzeDialogueUseCase
import com.bullconsulting.chinesereader.domain.usecase.GenerateDialogueUseCase
import com.bullconsulting.chinesereader.domain.usecase.LookupWordUseCase
import com.bullconsulting.chinesereader.domain.usecase.SearchDictionaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** An unknown word plus its meaning, for the reader's "unknown words" list. */
data class UnknownWordInfo(
    val word: String,
    val pinyin: String?,
    val definition: String?,
)

/** The possible states of the Generate screen at any moment. */
sealed interface GenerateUiState {
    data object Idle : GenerateUiState
    data object Loading : GenerateUiState
    data class Success(
        val text: String,
        val analyzed: AnalyzedDialogue,
        val unknownInfo: List<UnknownWordInfo>,
        val saved: Boolean = false,
    ) : GenerateUiState
    data class Error(val message: String) : GenerateUiState
}

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val generateDialogue: GenerateDialogueUseCase,
    private val analyzeDialogue: AnalyzeDialogueUseCase,
    private val lookupWord: LookupWordUseCase,
    private val addWord: AddWordUseCase,
    private val searchDictionary: SearchDictionaryUseCase,
    private val dialogues: DialogueRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenerateUiState>(GenerateUiState.Idle)
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    /** Candidate Chinese words from an English lookup (for the picker dialog). */
    private val _candidates = MutableStateFlow<List<DictionaryEntry>>(emptyList())
    val candidates: StateFlow<List<DictionaryEntry>> = _candidates.asStateFlow()

    private var lastTargets: List<String> = emptyList()

    fun searchEnglish(term: String) {
        viewModelScope.launch { _candidates.value = searchDictionary(term) }
    }

    fun clearCandidates() {
        _candidates.value = emptyList()
    }

    fun generate(rawTargets: String) {
        val targets = parseTargets(rawTargets)
        if (targets.isEmpty()) return
        lastTargets = targets
        runGeneration(targets, avoid = emptyList())
    }

    /** Re-generate, telling the model to avoid the unknown words from last time. */
    fun refine() {
        val current = _uiState.value as? GenerateUiState.Success ?: return
        if (lastTargets.isEmpty()) return
        runGeneration(lastTargets, avoid = current.analyzed.unknownWords)
    }

    /** Save the current dialogue to history. */
    fun save() {
        val current = _uiState.value as? GenerateUiState.Success ?: return
        if (current.saved) return
        viewModelScope.launch {
            dialogues.save(lastTargets, current.text, current.analyzed.knownRatio)
            _uiState.value = current.copy(saved = true)
        }
    }

    /** Add an unknown word to vocabulary, then re-grade the current dialogue. */
    fun addToVocabulary(hanzi: String) {
        val current = _uiState.value as? GenerateUiState.Success ?: return
        viewModelScope.launch {
            addWord(hanzi)
            _uiState.value = buildSuccess(current.text, lastTargets)
        }
    }

    private fun runGeneration(targets: List<String>, avoid: List<String>) {
        _uiState.value = GenerateUiState.Loading
        viewModelScope.launch {
            val result = generateDialogue(
                GenerationRequest(targetWords = targets, avoidWords = avoid),
            )
            _uiState.value = result.fold(
                onSuccess = { buildSuccess(it.text, targets) },
                onFailure = { GenerateUiState.Error(it.message ?: "Something went wrong.") },
            )
        }
    }

    /** Analyse the dialogue and look up meanings for its unknown words. */
    private suspend fun buildSuccess(text: String, targets: List<String>): GenerateUiState.Success {
        val analyzed = analyzeDialogue(text, targets)
        val info = analyzed.unknownWords.map { word ->
            val entry = lookupWord(word)
            UnknownWordInfo(
                word = word,
                pinyin = entry?.pinyin,
                definition = entry?.definitions?.firstOrNull(),
            )
        }
        return GenerateUiState.Success(text, analyzed, info)
    }

    private fun parseTargets(raw: String): List<String> =
        raw.split(Regex("[\\s,，、]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
