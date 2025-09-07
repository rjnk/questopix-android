package com.rejnek.oog.ui.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _openFromGame = MutableStateFlow(false)
    val openFromGame = _openFromGame.asStateFlow()

    private val _gameName = MutableStateFlow<String?>(null)
    val gameName = _gameName.asStateFlow()

    private val _gameDescription = MutableStateFlow<String?>(null)
    val gameDescription = _gameDescription.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.currentGamePackage.collect { pack ->
                if(pack != null) {
                    _openFromGame.value = true
                    _gameName.value = pack.getName()
                    _gameDescription.value = pack.info("description")
                } else {
                    _openFromGame.value = false
                }
            }
        }
    }

    fun pauseGame() {
        viewModelScope.launch {
            delay(400) // Small delay to allow UI to update
            gameRepository.pauseCurrentGame()
        }
    }

    fun quitGame() {
        viewModelScope.launch {
            gameRepository.quitCurrentGame()
        }
    }

    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}