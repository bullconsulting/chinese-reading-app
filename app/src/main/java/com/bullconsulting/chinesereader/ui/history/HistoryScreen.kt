package com.bullconsulting.chinesereader.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bullconsulting.chinesereader.domain.model.SavedDialogue
import com.bullconsulting.chinesereader.ui.common.HighlightedDialogueText
import com.bullconsulting.chinesereader.ui.common.openInPleco
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/** Browse saved dialogues; tap one to reopen it (re-graded), or delete it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val detail = viewModel.detail
    if (detail != null) {
        DialogueDetailView(
            targetWords = detail.saved.targetWords,
            knownRatio = detail.analyzed.knownRatio,
            analyzed = detail.analyzed,
            onBack = viewModel::closeDetail,
        )
        return
    }

    val dialogues by viewModel.dialogues.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("History") }) },
    ) { padding ->
        if (dialogues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("No saved dialogues yet. Generate one and tap Save.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                items(dialogues, key = { it.id }) { dialogue ->
                    HistoryRow(
                        dialogue = dialogue,
                        onOpen = { viewModel.open(dialogue) },
                        onDelete = { viewModel.delete(dialogue.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

@Composable
private fun HistoryRow(
    dialogue: SavedDialogue,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val targets = dialogue.targetWords.joinToString(" ").ifEmpty { "(no targets)" }
            Text(targets, style = MaterialTheme.typography.titleMedium)
            Text(
                text = dialogue.text.replace("\n", "  "),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val percent = (dialogue.knownRatio * 100).roundToInt()
            Text(
                text = "Known $percent%  ·  ${DATE_FORMAT.format(Date(dialogue.createdAt))}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        TextButton(onClick = onDelete) { Text("Delete") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogueDetailView(
    targetWords: List<String>,
    knownRatio: Float,
    analyzed: com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dialogue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            val percent = (knownRatio * 100).roundToInt()
            Text(
                text = "Targets: ${targetWords.joinToString(" ").ifEmpty { "—" }}",
                style = MaterialTheme.typography.labelLarge,
            )
            Text("Known $percent%", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(16.dp))
            val context = LocalContext.current
            HighlightedDialogueText(
                analyzed = analyzed,
                onWordClick = { word -> openInPleco(context, word) },
            )
        }
    }
}
