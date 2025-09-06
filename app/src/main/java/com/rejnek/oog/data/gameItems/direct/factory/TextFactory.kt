package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class TextFactory : GenericDirectFactory() {
    override val id = "text"

    override suspend fun create(data: String) {
        gameRepository?.addUIElement { MyText(data).Show() }
    }
}

class MyText(
    private val text: String
) {
    @Composable
    fun Show() {
        Text(
            text = text,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}