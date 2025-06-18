package com.rejnek.oog.data.gameItems

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class TextFactory : GenericGameFactory() {
    override val id = "text"
    override val js: String = """
        function ${id}(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

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