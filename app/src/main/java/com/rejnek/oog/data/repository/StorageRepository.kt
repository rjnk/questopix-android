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
 * @param context Application context for resource access
 */
class StorageRepository(
    private val context: Context
) {
    // Underlying storage
    private val gameStorage = GameStorage(context)

    // ========= Setup Operations ==========

    /**
     * Marks the initial setup as complete.
     */
    fun setSetupComplete() = gameStorage.setSetupComplete()

    /**
     * Checks if the initial setup is complete.
     * @return True if setup is complete, false otherwise
     */
    fun isSetupComplete() = gameStorage.isSetupComplete()

    // ========= Library Operations ==========
    /**
     * Adds a game to the library.
     * @param gamePackage GamePackage object to add
     */
    fun addGameToLibrary(gamePackage: GamePackage) {
        deleteAllImages(gamePackage.getId())
        gameStorage.addGameToLibrary(gamePackage)
    }

    /**
     * Removes a game from the library by its ID.
     * @param gameId ID of the game to remove
     */
    fun removeGameFromLibrary(gameId: String) {
        gameStorage.removeGameFromLibrary(gameId)
        deleteAllImages(gameId)
    }

    /**
     * Retrieves all games in the library.
     * @return List of GamePackage objects in the library
     */
    fun getLibraryGames() = gameStorage.getLibraryGames()

    // ========== Game Operations ==========

    /**
     * Saves the current state of a game package to storage.
     *
     * @param gamePackage GamePackage object to save
     */
    suspend fun saveGame(gamePackage: GamePackage): Unit = withContext(Dispatchers.IO) {
        gameStorage.addGameToLibrary(gamePackage)
        Log.d("StorageRepository", "Game ${gamePackage.getId()} saved with ${gamePackage.currentTaskId}")
    }

    /**
     * Retrieves a specific game package by its ID.
     *
     * @param gameId ID of the game to retrieve
     * @return GamePackage object if found, null otherwise
     */
    suspend fun getGameById(gameId: String): GamePackage? = withContext(Dispatchers.IO) {
        return@withContext gameStorage.getGameById(gameId)
    }

    /**
     * Retrieves the saved in-progress game package, if any.
     * @return GamePackage object if a saved game exists, null otherwise
     */
    suspend fun getSavedGamePackage(): GamePackage? = withContext(Dispatchers.IO) {
        try{
            return@withContext getLibraryGames().first { it.state == GameState.IN_PROGRESS }
        } catch (e: NoSuchElementException) {
            return@withContext null
        }
    }

    /**
     * Checks if there is a saved in-progress game.
     * @return True if a saved game exists, false otherwise
     */
    suspend fun hasSavedGame(): Boolean = withContext(Dispatchers.IO) {
        return@withContext getSavedGamePackage() != null
    }

    // ========== Image Operations ==========

    /**
     * Deletes all images associated with a specific game package.
     *
     * @param packageId ID of the game package whose images should be deleted
     */
    fun deleteAllImages(packageId: String) {
        val imagesDir = File(context.filesDir, "user_images/$packageId")
        if (imagesDir.exists()) {
            imagesDir.deleteRecursively()
        }
    }
}
