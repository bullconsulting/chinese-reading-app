package com.bullconsulting.chinesereader.ui.vocabulary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bullconsulting.chinesereader.domain.model.Word
import com.bullconsulting.chinesereader.ui.common.toAccentedPinyin

/** The vocabulary screen: import from Sheet, add words, see the list, delete words. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    viewModel: VocabularyViewModel = hiltViewModel(),
    importViewModel: ImportViewModel = hiltViewModel(),
) {
    val words by viewModel.words.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }

    // Launches Google's consent screen when authorization needs it.
    val consentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        importViewModel.onConsentResult(result.data)
    }
    LaunchedEffect(Unit) {
        importViewModel.consentRequest.collect { intentSender ->
            consentLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Vocabulary") },
                actions = {
                    TextButton(onClick = { importViewModel.begin() }) {
                        Text("Import")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Add a word (hanzi)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addWord(input)
                        input = ""
                    },
                ) {
                    Text("Add")
                }
            }

            Spacer(Modifier.height(16.dp))

            if (words.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No words yet. Add one above or Import from your Sheet.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(words, key = { it.hanzi }) { word ->
                        WordRow(word = word, onDelete = { viewModel.deleteWord(word.hanzi) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    ImportStatusDialog(state = importViewModel.state, onDismiss = importViewModel::dismiss)
}

@Composable
private fun ImportStatusDialog(state: ImportState, onDismiss: () -> Unit) {
    when (state) {
        is ImportState.Idle -> Unit
        is ImportState.Loading -> AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Importing…") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Reading your Google Sheet")
                }
            },
        )
        is ImportState.Success -> AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
            title = { Text("Import complete") },
            text = { Text("Added ${state.added} new word(s). Skipped ${state.skipped} already known.") },
        )
        is ImportState.Error -> AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
            title = { Text("Import failed") },
            text = { Text(state.message) },
        )
    }
}

@Composable
private fun WordRow(word: Word, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = word.hanzi, style = MaterialTheme.typography.titleMedium)
            val subtitle = listOfNotNull(
                word.pinyin?.let(::toAccentedPinyin),
                word.definition,
            ).joinToString(" · ")
            if (subtitle.isNotEmpty()) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}
