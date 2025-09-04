package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val gamesPackage = MutableStateFlow<List<GamePackage>>(emptyList())
    val libraryGames = gamesPackage.asStateFlow()

    init {
        loadLibraryGames()
    }

    private fun loadLibraryGames() {
        viewModelScope.launch {
            gamesPackage.value = gameRepository.getLibraryGames()
        }
    }

    fun onAddGameFromFile(gamePackage: GamePackage) {
        viewModelScope.launch {
            // Add to library
            gameRepository.addGameToLibrary(gamePackage)

            // Refresh the library list
            loadLibraryGames()
        }
    }

    fun refreshLibrary() {
        loadLibraryGames()
    }
}
