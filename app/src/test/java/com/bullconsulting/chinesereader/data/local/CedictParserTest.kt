package com.bullconsulting.chinesereader.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CedictParserTest {

    @Test
    fun parsesAWellFormedLine() {
        val entry = CedictParser.parseLine("你好 你好 [ni3 hao3] /hello/hi/")

        requireNotNull(entry)
        assertEquals("你好", entry.simplified)
        assertEquals("ni3 hao3", entry.pinyin)
        assertEquals("hello; hi", entry.definition)
    }

    @Test
    fun skipsCommentAndBlankLines() {
        assertNull(CedictParser.parseLine("# CC-CEDICT header"))
        assertNull(CedictParser.parseLine(""))
    }
}
