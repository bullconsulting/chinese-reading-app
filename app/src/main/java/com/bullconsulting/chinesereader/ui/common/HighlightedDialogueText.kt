package com.bullconsulting.chinesereader.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue
import com.bullconsulting.chinesereader.domain.model.TokenType

/**
 * Renders a dialogue with target words bold and unknown words highlighted.
 * Unknown words are tappable (via [onWordClick], e.g. to open Pleco).
 * Shared by the Generate screen and the History detail so both look identical.
 */
@Composable
fun HighlightedDialogueText(
    analyzed: AnalyzedDialogue,
    modifier: Modifier = Modifier,
    onWordClick: (String) -> Unit = {},
) {
    val highlight = MaterialTheme.colorScheme.tertiaryContainer
    val onHighlight = MaterialTheme.colorScheme.onTertiaryContainer
    val targetColor = MaterialTheme.colorScheme.primary

    val annotated = buildAnnotatedString {
        analyzed.tokens.forEach { token ->
            when (token.type) {
                TokenType.UNKNOWN -> withLink(
                    LinkAnnotation.Clickable(
                        tag = token.surface,
                        linkInteractionListener = { onWordClick(token.surface) },
                    ),
                ) {
                    withStyle(
                        SpanStyle(
                            background = highlight,
                            color = onHighlight,
                            textDecoration = TextDecoration.Underline,
                        ),
                    ) {
                        append(token.surface)
                    }
                }
                TokenType.TARGET -> withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, color = targetColor),
                ) { append(token.surface) }
                else -> append(token.surface)
            }
        }
    }
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge,
        lineHeight = 30.sp,
        modifier = modifier,
    )
}
