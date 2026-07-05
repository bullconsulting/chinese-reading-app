package com.bullconsulting.chinesereader.data.local

/**
 * Parses one line of CC-CEDICT into a row.
 * Line format:  傳統 传统 [chuan2 tong3] /tradition/traditional/
 * Kept as a pure function so it can be unit-tested without Android.
 */
object CedictParser {

    private val LINE = Regex("""^(\S+)\s+(\S+)\s+\[(.+?)]\s+/(.+)/\s*$""")

    fun parseLine(line: String): DictionaryEntity? {
        if (line.isBlank() || line.startsWith("#")) return null
        val match = LINE.find(line) ?: return null
        val (traditional, simplified, pinyin, definitions) = match.destructured
        return DictionaryEntity(
            simplified = simplified,
            traditional = traditional,
            pinyin = pinyin,
            definition = definitions.replace("/", "; "),
        )
    }
}
