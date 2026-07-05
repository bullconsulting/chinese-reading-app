package com.bullconsulting.chinesereader.data.remote.qwen

import com.bullconsulting.chinesereader.domain.model.Dialogue
import com.bullconsulting.chinesereader.domain.model.GenerationRequest
import com.bullconsulting.chinesereader.domain.model.MissingApiKeyException
import com.bullconsulting.chinesereader.domain.repository.DialogueGenerator
import com.bullconsulting.chinesereader.domain.repository.UserPreferences
import javax.inject.Inject

/**
 * Fulfills the DialogueGenerator contract by calling the Qwen API.
 * Reads the API key + model from encrypted preferences at call time.
 */
class QwenDialogueGenerator @Inject constructor(
    private val api: QwenApi,
    private val preferences: UserPreferences,
) : DialogueGenerator {

    override suspend fun generate(request: GenerationRequest): Dialogue {
        val apiKey = preferences.getApiKey()?.takeIf { it.isNotBlank() }
            ?: throw MissingApiKeyException()

        val chatRequest = ChatRequest(
            model = preferences.getModelId(),
            messages = listOf(
                ChatMessage(role = "system", content = QwenPrompt.systemMessage()),
                ChatMessage(role = "user", content = QwenPrompt.userMessage(request)),
            ),
        )

        val response = api.chat(authorization = "Bearer $apiKey", request = chatRequest)
        val text = response.choices.firstOrNull()?.message?.content.orEmpty().trim()
        return Dialogue(text = text, targetWords = request.targetWords)
    }
}
