package com.bullconsulting.chinesereader.data.segmentation

import org.junit.Assert.assertTrue
import org.junit.Test

class JiebaWordFrequencyTest {

    @Test
    fun moreCommonWordsWeighHigher() {
        val frequency = JiebaWordFrequency()

        // 朋友 (very common) should outweigh rarer synonyms.
        assertTrue(frequency.weight("朋友") > frequency.weight("友人"))
        assertTrue(frequency.weight("友") > frequency.weight("友人"))
        // An unknown string gets the floor weight.
        assertTrue(frequency.weight("朋友") > frequency.weight("zzz-not-a-word"))
    }
}
