package com.bullconsulting.chinesereader.domain.model

/**
 * The inputs for generating a dialogue.
 * `avoidWords` is used by the refine loop to tell the model which unknown words to drop.
 */
data class GenerationRequest(
    val targetWords: List<String>,
    val approxLength: Int = 120,
    val avoidWords: List<String> = emptyList(),
)
