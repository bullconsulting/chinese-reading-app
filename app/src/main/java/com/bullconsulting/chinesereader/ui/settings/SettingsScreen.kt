package com.bullconsulting.chinesereader.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/** Enter and save the Qwen API key + model id (stored encrypted). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            Text("Qwen API key", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.apiKey,
                onValueChange = viewModel::onApiKeyChange,
                label = { Text("API key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.modelId,
                onValueChange = viewModel::onModelIdChange,
                label = { Text("Model id") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
            Text("Google Sheet import", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.sheetUrl,
                onValueChange = viewModel::onSheetUrlChange,
                label = { Text("Google Sheet URL (tab: known_words)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.save() }) {
                Text("Save")
            }
            if (viewModel.saved) {
                Spacer(Modifier.height(8.dp))
                Text("Saved ✓", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Your key is stored encrypted on this device only. " +
                    "It is never sent anywhere except directly to Qwen.",
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(Modifier.height(24.dp))
            Text("About", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Dictionary data from CC-CEDICT (CC BY-SA 4.0). " +
                    "Word segmentation by Jieba (Apache-2.0).",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
