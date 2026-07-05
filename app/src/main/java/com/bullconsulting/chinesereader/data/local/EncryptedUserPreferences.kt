package com.bullconsulting.chinesereader.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bullconsulting.chinesereader.domain.repository.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores settings in an ENCRYPTED preferences file, backed by the Android Keystore.
 * The Qwen API key never touches disk in plain text.
 */
@Singleton
class EncryptedUserPreferences @Inject constructor(
    @ApplicationContext context: Context,
) : UserPreferences {

    private val prefs = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun getApiKey(): String? = prefs.getString(KEY_API, null)

    override fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API, key).apply()
    }

    override fun getModelId(): String =
        prefs.getString(KEY_MODEL, null)?.takeIf { it.isNotBlank() } ?: DEFAULT_MODEL

    override fun setModelId(id: String) {
        prefs.edit().putString(KEY_MODEL, id).apply()
    }

    override fun getSheetUrl(): String = prefs.getString(KEY_SHEET_URL, null).orEmpty()

    override fun setSheetUrl(url: String) {
        prefs.edit().putString(KEY_SHEET_URL, url).apply()
    }

    companion object {
        const val DEFAULT_MODEL = "qwen-plus"
        private const val KEY_API = "qwen_api_key"
        private const val KEY_MODEL = "qwen_model_id"
        private const val KEY_SHEET_URL = "google_sheet_url"
    }
}
