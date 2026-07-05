package com.bullconsulting.chinesereader.domain.model

/** How a single word relates to the user's vocabulary. */
enum class TokenType {
    TARGET, // one of the words the user asked for
    KNOWN, // already in the user's vocabulary
    UNKNOWN, // a Chinese word the user does not know yet -> highlight
    OTHER, // punctuation, spaces, latin letters -> render plainly
}

/** One piece of the dialogue after segmentation, tagged with its type. */
data class AnalyzedToken(
    val surface: String,
    val type: TokenType,
)

/**
 * The result of analysing a generated dialogue on-device.
 * `knownRatio` is the fraction of Chinese words the user already knows.
 */
data class AnalyzedDialogue(
    val tokens: List<AnalyzedToken>,
    val knownRatio: Float,
    val unknownWords: List<String>,
)
