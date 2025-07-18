package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class TextFactory : GenericDirectFactory() {
    override val id = "text"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement { MyText(data).Show() }
    }
}

class MyText(
    private val text: String
) {
    @Composable
    fun Show() {
        Text(text = text)
    }
}