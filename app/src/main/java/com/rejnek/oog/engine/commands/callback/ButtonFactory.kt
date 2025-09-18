package com.rejnek.oog.engine.commands.callback

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejnek.oog.engine.commands.GenericCallbackFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ButtonFactory() : GenericCallbackFactory() {
    override val id: String = "button"

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

        // Generate a random color from 3 Material theme colors that look good on light green
        val randomColorIndex = remember {
            Random.nextInt(3)
        }

        val selectedColor = when (randomColorIndex) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.tertiary
        }

        Button(
            onClick = onClick,
            modifier = modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = text)
        }
    }
}
