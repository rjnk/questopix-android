package com.rejnek.oog.data.repository

import android.content.Context
import android.util.Log
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.storage.GameStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository responsible for game storage operations
 */
class GameStorageRepository(
    private val context: Context
) {
    private val gameStorage = GameStorage(context)

    // Library Operations
    fun addGameToLibrary(gamePackage: GamePackage) {
        gameStorage.addGameToLibrary(gamePackage)
    }

    fun removeGameFromLibrary(gameId: String) {
        gameStorage.removeGameFromLibrary(gameId)
        deleteAllImages(gameId)
    }

    fun getLibraryGames() = gameStorage.getLibraryGames()

    // Game Operations
    suspend fun saveGame(gamePackage: GamePackage): Unit = withContext(Dispatchers.IO) {
        gameStorage.addGameToLibrary(gamePackage)
        Log.d("GameStorageRepository", "Game ${gamePackage.getId()} saved with ${gamePackage.currentTaskId}")
    }

    suspend fun getGameById(gameId: String): GamePackage? = withContext(Dispatchers.IO) {
        return@withContext gameStorage.getGameById(gameId)
    }

    suspend fun hasSavedGame(): Boolean = withContext(Dispatchers.IO) {
        return@withContext getSavedGamePackage() != null
    }

    suspend fun getSavedGamePackage(): GamePackage? = withContext(Dispatchers.IO) {
        try{
            return@withContext getLibraryGames().first { it.state == GameState.IN_PROGRESS }
        } catch (e: NoSuchElementException) {
            return@withContext null
        }
    }

    // Image Operations
    fun deleteAllImages(packageId: String) {
        val imagesDir = File(context.filesDir, "user_images/$packageId")
        if (imagesDir.exists()) {
            imagesDir.deleteRecursively()
        }
    }
}
