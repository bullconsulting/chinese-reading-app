package com.bullconsulting.chinesereader.ui.generate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bullconsulting.chinesereader.domain.model.DictionaryEntry
import com.bullconsulting.chinesereader.ui.common.toAccentedPinyin

/**
 * A dialog to look up an English word and pick the Chinese word you meant.
 * We never auto-translate — the user chooses, because one English gloss maps
 * to many Chinese words.
 */
@Composable
fun EnglishLookupDialog(
    candidates: List<DictionaryEntry>,
    onSearch: (String) -> Unit,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Look up an English word") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("English") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onSearch(query) }) { Text("Search") }
                }
                Spacer(Modifier.height(12.dp))

                if (candidates.isEmpty()) {
                    Text("Type an English word, tap Search, then pick the Chinese word.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 340.dp)) {
                        items(candidates, key = { it.simplified }) { candidate ->
                            CandidateRow(
                                candidate = candidate,
                                onPick = { onPick(candidate.simplified) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun CandidateRow(candidate: DictionaryEntry, onPick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPick)
            .padding(vertical = 10.dp),
    ) {
        Text(
            text = "${candidate.simplified}   ${toAccentedPinyin(candidate.pinyin)}",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = candidate.definitions.joinToString("; ").take(90),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
