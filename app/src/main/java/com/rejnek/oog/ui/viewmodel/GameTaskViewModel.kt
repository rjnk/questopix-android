package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _finishGame = MutableStateFlow(false)
    val finishGame = _finishGame.asSharedFlow()
    private val _gameName = MutableStateFlow("")
    val gameName = _gameName.asStateFlow()
    private val _gameState = MutableStateFlow(GameState.IN_PROGRESS)
    val gameState = _gameState.asStateFlow()
    private val _packNeedsLocation = MutableStateFlow(false)
    val isTaskRequiringLocation = _packNeedsLocation.asStateFlow()
    val locationPermissionGranted = gameRepository.locationRepository.isPermissionGranted

    // Expose UI elements from the repository
    val uiElements = gameRepository.gameUIRepository.uiElements


    fun refreshLocationPermission() {
        viewModelScope.launch {
            gameRepository.startLocationMonitoring()
        }
    }

    init {
        viewModelScope.launch {
            gameRepository.currentGamePackage.collect { pack ->
                if(pack != null) {
                    _gameName.value = pack.getName()
                    _gameState.value = pack.state
                    _packNeedsLocation.value = gameRepository.generateAreasForMonitoring(pack).isNotEmpty()

                    if(pack.state == GameState.FINISHED) {
                        _finishGame.value = true
                    }
                }
            }
        }
    }
}
