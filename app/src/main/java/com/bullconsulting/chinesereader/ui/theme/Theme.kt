package com.bullconsulting.chinesereader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The tertiary slot carries our "unknown word" highlight (blue container + underline color).
private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = OnRedPrimary,
    tertiary = BlueUnderline,
    tertiaryContainer = BlueHighlight,
    onTertiaryContainer = OnBlueHighlight,
)

private val DarkColors = darkColorScheme(
    primary = RedPrimaryDark,
    onPrimary = OnRedPrimaryDark,
    tertiary = BlueUnderlineDark,
    tertiaryContainer = BlueHighlightDark,
    onTertiaryContainer = OnBlueHighlightDark,
)

/** Wraps the whole app in our colors + fonts. Every screen calls this. */
@Composable
fun ChineseReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
