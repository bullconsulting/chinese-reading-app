package com.bullconsulting.chinesereader.domain.repository

import com.bullconsulting.chinesereader.domain.model.Dialogue
import com.bullconsulting.chinesereader.domain.model.GenerationRequest

/**
 * Contract for turning a request into a dialogue. The domain doesn't care that
 * Qwen is behind this — swap the model or add a backend without touching callers.
 */
interface DialogueGenerator {
    suspend fun generate(request: GenerationRequest): Dialogue
}
