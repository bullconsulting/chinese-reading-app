package com.bullconsulting.chinesereader.data.remote.qwen

import com.bullconsulting.chinesereader.domain.model.GenerationRequest

/**
 * Builds the text we send to Qwen. Kept separate from the network call so the
 * wording can change without touching how requests are sent.
 */
object QwenPrompt {

    fun systemMessage(): String = """
        You write short, natural dialogues in Simplified Chinese for a language learner.
        Rules:
        1. Use the learner's TARGET WORDS as naturally and as often as the topic allows.
        2. Keep all other vocabulary simple and high-frequency.
        3. Use about 2 speakers.
        4. Output ONLY the dialogue lines in the format "A: 中文" and "B: 中文".
           No pinyin, no translation, no explanations.
    """.trimIndent()

    fun userMessage(request: GenerationRequest): String {
        val targets = request.targetWords.joinToString("、")
        val avoid = if (request.avoidWords.isEmpty()) {
            ""
        } else {
            "\nAvoid these words the learner does not know: " +
                request.avoidWords.joinToString("、") + "."
        }
        return "TARGET WORDS: $targets\n" +
            "Approximate length: ${request.approxLength} Chinese characters.$avoid"
    }
}
