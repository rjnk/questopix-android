package com.rejnek.oog.data.engine.gameItems

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Button() : GenericGameItem() {
    override val id: String = "button"
    override val js: String = """
        function ${id}(buttonText, callback) {
            const callbackId = Android.registerCallback("$id", buttonText);
            
            // Store the user's callback to be executed when the button is clicked
            window._callbackResolvers[callbackId] = () => {
                callback();
                return ""; // Buttons don't return a value
            };
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        _buttonText.value = data
        _onButtonClick.value = {
            game?.resolveCallback(callbackId, "")
        }
        gameRepository?.addUIElement { Show() }
    }

    override fun clear() {
        // Nothing for now
    }

    private val _buttonText = MutableStateFlow("Button")
    val buttonText = _buttonText.asStateFlow()

    private val _onButtonClick = MutableStateFlow<() -> Unit>({})
    val onButtonClick = _onButtonClick.asStateFlow()

    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val text by buttonText.collectAsState()
        val onClick by onButtonClick.collectAsState()

        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
        }
    }
}

