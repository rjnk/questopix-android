package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.repository.UiCaptureExclusions

class FinishGameButtonFactory : GenericDirectFactory() {
    override val id = "finishGameButton"

    override suspend fun create(data: String) {
        var elementRef: (@Composable () -> Unit)? = null
        elementRef = {
            FinishGameButton(
                text = data,
                onClick = { gameRepository?.finishGame() }
            ).Show()
        }
        UiCaptureExclusions.excluded.add(elementRef)
        gameRepository?.addUIElement(elementRef)
    }
}

class FinishGameButton(
    private val text: String,
    private val onClick: () -> Unit
) {
    @Composable
    fun Show() {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 24.sp
            )
        }
    }
}