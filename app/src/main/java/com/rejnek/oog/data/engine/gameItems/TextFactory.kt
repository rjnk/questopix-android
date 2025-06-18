package com.rejnek.oog.data.engine.gameItems

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class TextFactory : GenericGameItem() {
    override val id = "text"
    override val js: String = """
        function ${id}(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.addUIElement { MyText(data).Show() }
    }

    override fun clear() {
        // blank
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