package com.bullconsulting.chinesereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bullconsulting.chinesereader.ui.MainScaffold
import com.bullconsulting.chinesereader.ui.theme.ChineseReaderTheme
import dagger.hilt.android.AndroidEntryPoint

/** The single screen (Activity) the app launches into. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChineseReaderTheme {
                MainScaffold()
            }
        }
    }
}
