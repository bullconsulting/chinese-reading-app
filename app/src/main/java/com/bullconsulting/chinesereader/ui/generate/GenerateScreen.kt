package com.bullconsulting.chinesereader.ui.generate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bullconsulting.chinesereader.domain.model.AnalyzedDialogue
import com.bullconsulting.chinesereader.domain.util.containsHan
import com.bullconsulting.chinesereader.ui.common.HighlightedDialogueText
import com.bullconsulting.chinesereader.ui.common.openInPleco
import com.bullconsulting.chinesereader.ui.common.toAccentedPinyin
import kotlin.math.roundToInt

/** Enter target words, generate a dialogue, and read it with unknown words highlighted. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val candidates by viewModel.candidates.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    var showLookup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Generate") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Target word(s), e.g. 喜欢 旅行") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            val hasHan = input.containsHan()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { viewModel.generate(input) },
                    enabled = state !is GenerateUiState.Loading && hasHan,
                ) {
                    Text("Generate dialogue")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { showLookup = true }) {
                    Text("Look up English")
                }
            }
            if (input.isNotBlank() && !hasHan) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Enter Chinese characters, or use “Look up English”.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(20.dp))

            when (val s = state) {
                is GenerateUiState.Idle ->
                    Text("Enter target words above, then tap Generate.")
                is GenerateUiState.Loading ->
                    CircularProgressIndicator()
                is GenerateUiState.Success ->
                    DialogueResult(
                        analyzed = s.analyzed,
                        unknownInfo = s.unknownInfo,
                        saved = s.saved,
                        onSave = viewModel::save,
                        onRefine = viewModel::refine,
                        onAddWord = viewModel::addToVocabulary,
                    )
                is GenerateUiState.Error ->
                    Text(
                        text = "Error: ${s.message}",
                        color = MaterialTheme.colorScheme.error,
                    )
            }
        }
    }

    if (showLookup) {
        EnglishLookupDialog(
            candidates = candidates,
            onSearch = viewModel::searchEnglish,
            onPick = { hanzi ->
                input = if (input.isBlank()) hanzi else "$input $hanzi"
                showLookup = false
                viewModel.clearCandidates()
            },
            onDismiss = {
                showLookup = false
                viewModel.clearCandidates()
            },
        )
    }
}

@Composable
private fun DialogueResult(
    analyzed: AnalyzedDialogue,
    unknownInfo: List<UnknownWordInfo>,
    saved: Boolean,
    onSave: () -> Unit,
    onRefine: () -> Unit,
    onAddWord: (String) -> Unit,
) {
    val percent = (analyzed.knownRatio * 100).roundToInt()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Known $percent%  ·  ${analyzed.unknownWords.size} unknown",
            style = MaterialTheme.typography.labelLarge,
        )
        if (saved) {
            Text("Saved ✓", color = MaterialTheme.colorScheme.primary)
        } else {
            TextButton(onClick = onSave) { Text("Save") }
        }
    }
    Spacer(Modifier.height(12.dp))

    val context = LocalContext.current
    HighlightedDialogueText(
        analyzed = analyzed,
        onWordClick = { word -> openInPleco(context, word) },
    )

    if (unknownInfo.isNotEmpty()) {
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRefine) {
            Text("Refine (avoid unknown words)")
        }
        Spacer(Modifier.height(16.dp))
        Text("Unknown words", style = MaterialTheme.typography.titleSmall)
        unknownInfo.forEach { info ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val header = listOfNotNull(
                        info.word,
                        info.pinyin?.let(::toAccentedPinyin),
                    ).joinToString("  ")
                    Text(header, style = MaterialTheme.typography.bodyLarge)
                    if (info.definition != null) {
                        Text(info.definition, style = MaterialTheme.typography.bodySmall)
                    }
                }
                OutlinedButton(onClick = { onAddWord(info.word) }) {
                    Text("Add")
                }
            }
        }
    }
}
