package com.bullconsulting.chinesereader.domain.repository

/**
 * Contract for reading/writing user settings, including the (secret) Qwen API key.
 * The concrete store encrypts sensitive values; callers just see this interface.
 */
interface UserPreferences {
    fun getApiKey(): String?
    fun setApiKey(key: String)
    fun getModelId(): String
    fun setModelId(id: String)
    fun getSheetUrl(): String
    fun setSheetUrl(url: String)
}
