package com.bullconsulting.chinesereader.data.segmentation

import com.huaban.analysis.jieba.WordDictionary
import com.bullconsulting.chinesereader.domain.repository.WordFrequency
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Word-commonness weights, reused from Jieba's bundled dictionary (dict.txt) —
 * which is already loaded in memory for segmentation, so this adds no assets and
 * no extra load. Unknown words get the lowest possible weight.
 */
@Singleton
class JiebaWordFrequency @Inject constructor() : WordFrequency {

    // getInstance() loads dict.txt (if not already) and populates the freqs map.
    private val freqs: Map<String, Double> = WordDictionary.getInstance().freqs

    override fun weight(word: String): Double = freqs[word] ?: Double.NEGATIVE_INFINITY
}
