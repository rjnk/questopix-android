package com.rejnek.oog.ui.components.library

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.content.Context
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.zip.ZipInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

/**
 * Load all bundled games placed under assets/bundled_games/ and import them
 * Place a folder into: app/src/main/assets/bundled_games/your-game-folder
 */
fun loadBundledGames(context: Context): List<GamePackage> {
    val imported = mutableListOf<GamePackage>()

    val assetManager = context.assets
    val basePath = "bundled_games"
    val imageExtensions = setOf("png", "jpg", "jpeg", "gif")

    // helper values above; no unused functions

    fun processAssetFolder(gameFolder: String) {
        val folderPath = "$basePath/$gameFolder"
        try {
            // collect all file paths under the asset folder (iterative)
            val paths = mutableListOf<String>()
            val stack = ArrayDeque<String>().apply { add(folderPath) }
            while (stack.isNotEmpty()) {
                val p = stack.removeLast()
                val children = try { assetManager.list(p) } catch (_: Exception) { null }
                if (children == null || children.isEmpty()) {
                    // file
                    paths.add(p)
                } else {
                    for (c in children) stack.add("$p/$c")
                }
            }

            if (paths.isEmpty()) {
                Log.w("GameFilePicker", "No files found in bundled game folder: $gameFolder")
                return
            }

            val collected = paths.mapNotNull { path ->
                val name = File(path).name
                try {
                    assetManager.open(path).use { stream -> name to stream.readBytes() }
                } catch (e: Exception) {
                    Log.e("GameFilePicker", "Failed to read asset file: $path", e)
                    null
                }
            }

            if (collected.isEmpty()) {
                Log.w("GameFilePicker", "No readable files in bundled game folder: $gameFolder")
                return
            }

            // Use shared extractor to process collected entries
            try {
                val pkg = extractGameFromEntries(context.filesDir, collected.asSequence(), imageExtensions)
                imported.add(pkg)
            } catch (e: Exception) {
                Log.e("GameFilePicker", "Error importing bundled game: $gameFolder", e)
            }

        } catch (e: Exception) {
            Log.e("GameFilePicker", "Error accessing bundled game folder: $gameFolder", e)
        }
    }

    try {
        val gameFolders = assetManager.list(basePath) ?: emptyArray()
        for (folder in gameFolders) {
            // only process top-level folders; ensure it's a folder containing STANDARD
            processAssetFolder(folder)
        }
    } catch (e: Exception) {
        Log.e("GameFilePicker", "Error listing bundled games", e)
    }

    return imported
}

// Shared extractor: accepts a sequence of (name, bytes) pairs and writes images to filesDir/game_images/<id>
private fun extractGameFromEntries(baseDir: File, entries: Sequence<Pair<String, ByteArray>>, imageExtensions: Set<String>): GamePackage {
    var gameCode = ""
    var gameInfo = JsonObject(emptyMap())

    // Create temporary directory
    val tempDir = File(baseDir, "temp_${System.currentTimeMillis()}").apply { mkdirs() }

    try {
        for ((name, bytes) in entries) {
            when {
                name.endsWith("game.js") -> gameCode = bytes.toString(Charsets.UTF_8)
                name.endsWith("info.json") -> gameInfo = Json.decodeFromString(JsonObject.serializer(), bytes.toString(Charsets.UTF_8))
                File(name).extension.lowercase() in imageExtensions -> {
                    val imageFile = File(tempDir, File(name).name)
                    FileOutputStream(imageFile).use { it.write(bytes) }
                }
            }
        }

        val gameId = gameInfo["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Game ID missing in info.json")
        val gameImagesDir = File(baseDir, "game_images/$gameId").apply {
            if (exists()) deleteRecursively()
            mkdirs()
        }

        tempDir.listFiles()?.forEach { tempFile -> if (tempFile.isFile) tempFile.renameTo(File(gameImagesDir, tempFile.name)) }

        return GamePackage(gameInfo, gameCode, GameState.NOT_STARTED, System.currentTimeMillis())

    } finally {
        tempDir.deleteRecursively()
    }
}

private fun extractGameFromZip(baseDir: File, inputStream: InputStream): GamePackage {
    val imageExtensions = setOf("png", "jpg", "jpeg", "gif")

    // Collect zip entries as (name, bytes)
    val collected = mutableListOf<Pair<String, ByteArray>>()

    ZipInputStream(inputStream.buffered()).use { zipStream ->
        generateSequence { zipStream.nextEntry }
            .filterNot { it.isDirectory }
            .forEach { entry ->
                val name = entry.name
                val baos = java.io.ByteArrayOutputStream()
                zipStream.copyTo(baos)
                collected.add(name to baos.toByteArray())
            }
    }

    return extractGameFromEntries(baseDir, collected.asSequence(), imageExtensions)
}
