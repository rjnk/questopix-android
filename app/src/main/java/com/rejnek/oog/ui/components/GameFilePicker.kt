package com.rejnek.oog.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun rememberGameFilePicker(
    onGameFileSelected: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val gameCode = context.contentResolver.openInputStream(it)?.use { inputStream ->
                    inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } ?: return@let

                onGameFileSelected(gameCode)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error loading game file: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    return { filePickerLauncher.launch("*/*") }
}
