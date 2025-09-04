package com.rejnek.oog.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import java.util.zip.ZipInputStream
import java.io.File
import java.io.FileOutputStream

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
                Log.d("GameFilePicker", "File selected: $uri")
                val gameData = context.contentResolver.openInputStream(it)?.use { inputStream ->
                    extractGameFromZip(context.filesDir, inputStream)
                } ?: run {
                    Log.e("GameFilePicker", "Failed to open input stream")
                    return@let
                }

                onGameFileSelected(gameData)
            } catch (e: Exception) {
                Log.e("GameFilePicker", "Error loading game file", e)
                Toast.makeText(
                    context,
                    "Error loading game file: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    return { filePickerLauncher.launch("application/zip") }
}

private fun extractGameFromZip(baseDir: File, inputStream: java.io.InputStream): String {
    val gameId = "hra" //TODO read from js
    val gameImagesDir = File(baseDir, "game_images/$gameId").apply { mkdirs() }
    val imageExtensions = setOf("png", "jpg", "jpeg", "gif")

    return ZipInputStream(inputStream.buffered()).use { zipStream ->
        var gameCode = ""

        generateSequence { zipStream.nextEntry }
            .filterNot { it.isDirectory }
            .forEach { entry ->
                when {
                    entry.name.endsWith("game.js") -> {
                        gameCode = zipStream.bufferedReader().readText()
                    }
                    File(entry.name).extension.lowercase() in imageExtensions -> {
                        val imageFile = File(gameImagesDir, File(entry.name).name)
                        FileOutputStream(imageFile).use { zipStream.copyTo(it) }
                    }
                }
            }

        gameCode
    }
}