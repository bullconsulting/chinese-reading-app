package com.bullconsulting.chinesereader.domain.usecase

import com.bullconsulting.chinesereader.domain.model.Dialogue
import com.bullconsulting.chinesereader.domain.model.GenerationRequest
import com.bullconsulting.chinesereader.domain.repository.DialogueGenerator
import javax.inject.Inject

/**
 * One app action: generate a dialogue. Wraps the result in Kotlin's `Result`
 * so callers get either success or a captured error, never a crash.
 */
class GenerateDialogueUseCase @Inject constructor(
    private val generator: DialogueGenerator,
) {
    suspend operator fun invoke(request: GenerationRequest): Result<Dialogue> =
        runCatching { generator.generate(request) }
}
