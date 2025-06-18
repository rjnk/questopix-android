package com.rejnek.oog.data.engine.gameItems

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HeadingFactory : GenericGameItem() {
    override val id = "heading"
    override val js: String = """
        function ${id}(elementId) {
            directAction("$id", elementId);
        }
    """.trimIndent()

    override suspend fun run(data: String, callbackId: String) {
        gameRepository?.addUIElement { MyHeading(data).Show() }
    }

    override fun clear() {
        // blank
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