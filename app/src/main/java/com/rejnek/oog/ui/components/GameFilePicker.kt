package com.rejnek.oog.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.rejnek.oog.data.model.GamePackage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.zip.ZipInputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun rememberGameFilePicker(
    onGameFileSelected: (GamePackage) -> Unit
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

private fun extractGameFromZip(baseDir: File, inputStream: java.io.InputStream): GamePackage {
    var gameCode = ""
    var gameInfo = JsonObject(emptyMap())
    val imageExtensions = setOf("png", "jpg", "jpeg", "gif")

    // Create temporary directory
    val tempDir = File(baseDir, "temp_${System.currentTimeMillis()}").apply { mkdirs() }

    try {
        // Extract everything to temp folder
        ZipInputStream(inputStream.buffered()).use { zipStream ->
            generateSequence { zipStream.nextEntry }
                .filterNot { it.isDirectory }
                .forEach { entry ->
                    when {
                        entry.name.endsWith("game.js") -> {
                            gameCode = zipStream.bufferedReader().readText()
                        }
                        entry.name.endsWith("info.json") -> {
                            gameInfo = Json.decodeFromString(
                                JsonObject.serializer(),
                                zipStream.bufferedReader().readText()
                            )
                        }
                        File(entry.name).extension.lowercase() in imageExtensions -> {
                            val imageFile = File(tempDir, File(entry.name).name)
                            FileOutputStream(imageFile).use { zipStream.copyTo(it) }
                        }
                    }
                }
        }

        // Get game ID and create final directory
        val gameId = gameInfo["id"]?.jsonPrimitive?.content ?: throw IllegalStateException("Game ID missing in info.json")
        val gameImagesDir = File(baseDir, "game_images/$gameId").apply {
            if (exists()) deleteRecursively()
            mkdirs()
        }

        // Move images from temp to final location
        tempDir.listFiles()?.forEach { tempFile ->
            if (tempFile.isFile) {
                tempFile.renameTo(File(gameImagesDir, tempFile.name))
            }
        }

        return GamePackage(gameInfo, gameCode, System.currentTimeMillis())

    } finally {
        // Clean up temp directory
        tempDir.deleteRecursively()
    }
}