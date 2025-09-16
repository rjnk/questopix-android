package com.rejnek.oog.engine.commands.direct.factory

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.rejnek.oog.engine.commands.GenericDirectFactory

class HeadingFactory : GenericDirectFactory() {
    override val id = "heading"

    override suspend fun createWithArgs(args: List<String>) {
        val text = args.getOrNull(0) ?: ""
        val alignment = when (args.getOrNull(1)?.lowercase()) {
            "center" -> TextAlign.Center
            "end" -> TextAlign.End
            else -> TextAlign.Start
        }

        gameRepository?.addUIElement { MyHeading(text, alignment).Show() }
    }
}

class MyHeading(
    private val text: String,
    private val alignment: TextAlign = TextAlign.Start
) {
    @Composable
    fun Show() {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 32.sp,
            textAlign = alignment,
            modifier = Modifier.fillMaxWidth()
        )
    }
}