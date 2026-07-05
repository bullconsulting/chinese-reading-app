package com.bullconsulting.chinesereader.domain.util

/** True if this character is a Han (Chinese) character. */
fun Char.isHan(): Boolean =
    Character.UnicodeScript.of(this.code) == Character.UnicodeScript.HAN

/** True if the string contains at least one Han character. */
fun String.containsHan(): Boolean = any { it.isHan() }
