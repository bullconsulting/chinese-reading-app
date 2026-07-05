package com.bullconsulting.chinesereader.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bullconsulting.chinesereader.ui.generate.GenerateScreen
import com.bullconsulting.chinesereader.ui.history.HistoryScreen
import com.bullconsulting.chinesereader.ui.settings.SettingsScreen
import com.bullconsulting.chinesereader.ui.vocabulary.VocabularyScreen

private data class Tab(val label: String, val icon: ImageVector)

/**
 * The app's top-level shell: a bottom navigation bar that switches between the
 * three main screens. (A simple state swap; can grow into full navigation later.)
 */
@Composable
fun MainScaffold() {
    val tabs = listOf(
        Tab("Vocab", Icons.AutoMirrored.Filled.List),
        Tab("Generate", Icons.Filled.Edit),
        Tab("History", Icons.Filled.DateRange),
        Tab("Settings", Icons.Filled.Settings),
    )
    var selected by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (selected) {
                0 -> VocabularyScreen()
                1 -> GenerateScreen()
                2 -> HistoryScreen()
                else -> SettingsScreen()
            }
        }
    }
}
