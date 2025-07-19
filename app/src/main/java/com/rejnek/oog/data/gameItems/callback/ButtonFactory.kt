package com.rejnek.oog.data.gameItems.callback

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.rejnek.oog.data.gameItems.GenericItemFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ButtonFactory() : GenericItemFactory() {
    override val id: String = "button"
    override val js: String = """
        function ${id}(buttonText, callback) {
            const callbackId = Android.registerCallback("$id", buttonText);
            
            window._callbackResolvers[callbackId] = () => {
                callback();
                return "";
            };
        }
    """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        val btn = Button(
            text = data,
            onClick = { game?.resolveCallback(callbackId, "") }
        )
        gameRepository?.addUIElement {
            btn.Show()
        }
    }
}

class Button(
    text: String,
    onClick: () -> Unit
) {
    private val _buttonText = MutableStateFlow(text)
    val buttonText = _buttonText.asStateFlow()

    private val _onButtonClick = MutableStateFlow<() -> Unit>(onClick)
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

