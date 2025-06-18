package com.rejnek.oog.data.gameItems

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

class HeadingFactory : GenericGameFactory() {
    override val id = "heading"
    override val js: String = """
        function ${id}(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement { MyHeading(data).Show() }
    }
}

class MyHeading(
    private val text: String
) {
    @Composable
    fun Show() {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 24.sp
        )
    }
}