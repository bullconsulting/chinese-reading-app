package com.bullconsulting.chinesereader.domain.repository

/**
 * Contract for splitting Chinese text into words. Chinese has no spaces, so we
 * need a segmenter. Kept behind an interface so the engine (Jieba) can be swapped.
 */
interface Segmenter {
    fun segment(text: String): List<String>
}
