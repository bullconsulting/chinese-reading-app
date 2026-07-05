package com.bullconsulting.chinesereader.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class PinyinTest {

    @Test
    fun placesToneMarkByRule() {
        assertEquals("rén", toAccentedPinyin("ren2"))
        assertEquals("hǎo", toAccentedPinyin("hao3"))
        assertEquals("huì", toAccentedPinyin("hui4")) // "ui" -> mark the i
        assertEquals("jiǔ", toAccentedPinyin("jiu3")) // "iu" -> mark the u
        assertEquals("guó", toAccentedPinyin("guo2")) // last vowel
        assertEquals("xiè", toAccentedPinyin("xie4")) // e wins over i
    }

    @Test
    fun handlesUmlautAndNeutralTone() {
        assertEquals("lǚ", toAccentedPinyin("lu:3"))
        assertEquals("de", toAccentedPinyin("de5")) // neutral tone: no mark
        assertEquals("r", toAccentedPinyin("r5")) // erhua
    }

    @Test
    fun convertsMultiSyllableStrings() {
        assertEquals("nǐ hǎo", toAccentedPinyin("ni3 hao3"))
        assertEquals("rén kǒu", toAccentedPinyin("ren2 kou3"))
    }
}
