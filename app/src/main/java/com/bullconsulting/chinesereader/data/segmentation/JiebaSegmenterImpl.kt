package com.bullconsulting.chinesereader.data.segmentation

import com.huaban.analysis.jieba.JiebaSegmenter as HuabanJieba
import com.bullconsulting.chinesereader.domain.repository.Segmenter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fulfills the Segmenter contract using the Jieba library. Marked @Singleton
 * because building it loads a dictionary — we want to do that only once.
 */
@Singleton
class JiebaSegmenterImpl @Inject constructor() : Segmenter {

    private val engine = HuabanJieba()

    override fun segment(text: String): List<String> =
        engine.process(text, HuabanJieba.SegMode.SEARCH).map { it.word }
}
