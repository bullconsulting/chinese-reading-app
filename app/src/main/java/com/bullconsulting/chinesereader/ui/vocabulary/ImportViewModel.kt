package com.bullconsulting.chinesereader.ui.vocabulary

import android.content.Intent
import android.content.IntentSender
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bullconsulting.chinesereader.data.remote.sheets.GoogleAuthorizer
import com.bullconsulting.chinesereader.domain.repository.UserPreferences
import com.bullconsulting.chinesereader.domain.usecase.ImportFromSheetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** State of a Google Sheet import at any moment. */
sealed interface ImportState {
    data object Idle : ImportState
    data object Loading : ImportState
    data class Success(val added: Int, val skipped: Int) : ImportState
    data class Error(val message: String) : ImportState
}

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val authorizer: GoogleAuthorizer,
    private val importFromSheet: ImportFromSheetUseCase,
    private val preferences: UserPreferences,
) : ViewModel() {

    var state by mutableStateOf<ImportState>(ImportState.Idle)
        private set

    // One-shot request for the screen to launch the Google consent screen.
    private val _consentRequest = MutableSharedFlow<IntentSender>(extraBufferCapacity = 1)
    val consentRequest = _consentRequest.asSharedFlow()

    fun begin() {
        if (preferences.getSheetUrl().isBlank()) {
            state = ImportState.Error("Add your Google Sheet URL in Settings first.")
            return
        }
        state = ImportState.Loading
        viewModelScope.launch {
            try {
                val result = authorizer.authorize()
                val pendingIntent = result.pendingIntent
                if (result.hasResolution() && pendingIntent != null) {
                    _consentRequest.emit(pendingIntent.intentSender)
                } else {
                    finishImport(result.accessToken)
                }
            } catch (e: Exception) {
                state = ImportState.Error(e.message ?: "Google authorization failed.")
            }
        }
    }

    fun onConsentResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val token = data?.let { authorizer.tokenFromIntent(it) }
                finishImport(token)
            } catch (e: Exception) {
                state = ImportState.Error(e.message ?: "Google authorization failed.")
            }
        }
    }

    private suspend fun finishImport(accessToken: String?) {
        if (accessToken.isNullOrBlank()) {
            state = ImportState.Error("Authorization was cancelled.")
            return
        }
        state = ImportState.Loading
        val result = importFromSheet(accessToken, preferences.getSheetUrl())
        state = result.fold(
            onSuccess = { ImportState.Success(it.added, it.skipped) },
            onFailure = { ImportState.Error(it.message ?: "Import failed.") },
        )
    }

    fun dismiss() {
        state = ImportState.Idle
    }
}
