package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class FinishGameButtonFactory : GenericDirectFactory() {
    override val id = "finishGameButton"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement {
            FinishGameButton(
                text = data,
                onClick = { gameRepository?.finishGame() }
            ).Show()
        }
    }
}

class FinishGameButton(
    private val text: String,
    private val onClick: () -> Unit
) {
    @Composable
    fun Show() {
        Button(
            onClick = onClick
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 24.sp
            )
        }
    }
}