package com.bullconsulting.chinesereader.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Opens a word in the Pleco dictionary app via its documented URL scheme
 * (plecoapi://x-callback-url/s?q=WORD). Falls back to a toast if Pleco isn't installed.
 */
fun openInPleco(context: Context, word: String) {
    val uri = Uri.parse("plecoapi://x-callback-url/s?q=" + Uri.encode(word))
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Pleco is not installed.", Toast.LENGTH_SHORT).show()
    }
}
