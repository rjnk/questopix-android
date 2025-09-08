package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameLocationRepository
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameInfoViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // Game package containing all game info
    private val _gamePackage = MutableStateFlow<GamePackage?>(null)
    val gamePackage = _gamePackage.asStateFlow()
    // Location permission state
    val locationPermissionGranted = gameRepository.gameLocationRepository.isPermissionGranted
    private val _isTaskRequiringLocation = MutableStateFlow(false)
    val isTaskRequiringLocation = _isTaskRequiringLocation.asStateFlow()
    // Toast
    private val _showNoLocationToast = MutableStateFlow(false)
    val showNoLocationToast = _showNoLocationToast.asStateFlow()
    private val _showFarAwayToast = MutableStateFlow(false)
    val showFarAwayToast = _showFarAwayToast.asStateFlow()

    fun loadGameInfo(gameId: String, onGameStarted: () -> Unit) {
        viewModelScope.launch {
            val games = gameRepository.gameStorageRepository.getLibraryGames()
            val tempPackage = games.find { it.getId() == gameId }

            // if game is in progress, we just open the game
            if (tempPackage?.state != GameState.NOT_STARTED) {
                openGame(onGameStarted, gameId)
                return@launch
            }

            // set the variables for the game info screen
            _gamePackage.value = tempPackage
            _isTaskRequiringLocation.value = tempPackage.getStartLocation() != null

            // start location service if needed
            if(_isTaskRequiringLocation.value){
                gameRepository.gameLocationRepository.startLocationService()
            }
        }
    }

    fun openGame(onGameStarted: () -> Unit, gameId: String) {
        viewModelScope.launch {
            gameRepository.initializeGameFromLibrary(gameId)
            onGameStarted()
        }
    }

    fun startGameByPressingButton(onGameStarted: () -> Unit){
        val gameId = gamePackage.value?.getId() ?: return
        val startLocation = gamePackage.value?.getStartLocation()

        // the game doesn't require location, we can start it right away
        if(startLocation == null) {
            openGame(onGameStarted, gameId)
            return
        }

        // we need to have current location
        val currentLocation = gameRepository.gameLocationRepository.currentLocation.value
        if(currentLocation == null) {
            _showNoLocationToast.value = true
            return
        }

        // the game requires location, we check if we are close enough to the start location
        val distance = GameLocationRepository.calculateDistance(currentLocation, startLocation)
        Log.d("GameInfoViewModel", "Distance to start location: $distance meters")
        if(distance <= 60.0) {
            openGame(onGameStarted, gameId)
        } else {
            _showFarAwayToast.value = true
        }
    }

    fun refreshLocationPermission() {
        gameRepository.gameLocationRepository.startLocationService()
    }

    // Toast visibility reset helpers
    fun farAwayToastShown() { _showFarAwayToast.value = false }
    fun noLocationToastShown() { _showNoLocationToast.value = false }
}