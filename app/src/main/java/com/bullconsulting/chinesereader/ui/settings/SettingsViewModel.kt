package com.bullconsulting.chinesereader.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bullconsulting.chinesereader.domain.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences,
) : ViewModel() {

    var apiKey by mutableStateOf(preferences.getApiKey().orEmpty())
        private set

    var modelId by mutableStateOf(preferences.getModelId())
        private set

    var sheetUrl by mutableStateOf(preferences.getSheetUrl())
        private set

    var saved by mutableStateOf(false)
        private set

    fun onApiKeyChange(value: String) {
        apiKey = value
        saved = false
    }

    fun onModelIdChange(value: String) {
        modelId = value
        saved = false
    }

    fun onSheetUrlChange(value: String) {
        sheetUrl = value
        saved = false
    }

    fun save() {
        preferences.setApiKey(apiKey.trim())
        preferences.setModelId(modelId.trim())
        preferences.setSheetUrl(sheetUrl.trim())
        saved = true
    }
}
