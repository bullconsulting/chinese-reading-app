package com.bullconsulting.chinesereader.ui.common

/**
 * Converts numbered CC-CEDICT pinyin (e.g. "ren2 kou3", "lu:3") into accented
 * pinyin ("rén kǒu", "lǚ") for DISPLAY only.
 *
 * We keep the numbered form as the stored/canonical data (lossless, searchable,
 * comparable) and derive the pretty form here at the presentation edge.
 *
 * Tone-mark placement rule: a > e > the o in "ou" > otherwise the last vowel.
 */
fun toAccentedPinyin(numbered: String): String =
    numbered.trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .joinToString(" ") { accentSyllable(it) }

private val TONE_MARKS: Map<Char, CharArray> = mapOf(
    'a' to charArrayOf('ā', 'á', 'ǎ', 'à'),
    'e' to charArrayOf('ē', 'é', 'ě', 'è'),
    'i' to charArrayOf('ī', 'í', 'ǐ', 'ì'),
    'o' to charArrayOf('ō', 'ó', 'ǒ', 'ò'),
    'u' to charArrayOf('ū', 'ú', 'ǔ', 'ù'),
    'ü' to charArrayOf('ǖ', 'ǘ', 'ǚ', 'ǜ'),
)

private const val VOWELS = "aeiouü"

private fun accentSyllable(raw: String): String {
    val tone = raw.lastOrNull()?.digitToIntOrNull()
    val bodyRaw = if (tone != null) raw.dropLast(1) else raw
    // CC-CEDICT writes ü as "u:" (occasionally "v").
    val body = bodyRaw.replace("u:", "ü").replace("U:", "Ü").replace("v", "ü")

    // Neutral tone (0/5) or no tone: no mark, but keep the ü substitution.
    if (tone == null || tone < 1 || tone > 4) return body

    val index = vowelIndexToMark(body)
    if (index < 0) return body
    val vowel = body[index]
    val accented = TONE_MARKS[vowel.lowercaseChar()]?.getOrNull(tone - 1) ?: return body
    val cased = if (vowel.isUpperCase()) accented.uppercaseChar() else accented
    return body.substring(0, index) + cased + body.substring(index + 1)
}

private fun vowelIndexToMark(body: String): Int {
    val lower = body.lowercase()
    lower.indexOf('a').let { if (it >= 0) return it }
    lower.indexOf('e').let { if (it >= 0) return it }
    lower.indexOf("ou").let { if (it >= 0) return it }
    for (i in lower.indices.reversed()) {
        if (lower[i] in VOWELS) return i
    }
    return -1
}
