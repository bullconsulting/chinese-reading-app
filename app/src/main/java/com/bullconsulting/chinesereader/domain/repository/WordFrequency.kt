package com.bullconsulting.chinesereader.domain.repository

/**
 * Provides a relative commonness weight for a Chinese word.
 * Higher = more common. Used to rank reverse-lookup candidates.
 */
interface WordFrequency {
    fun weight(word: String): Double
}
