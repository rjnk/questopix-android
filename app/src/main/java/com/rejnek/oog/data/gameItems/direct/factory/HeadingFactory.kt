package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class HeadingFactory : GenericDirectFactory() {
    override val id = "heading"

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
            fontSize = 32.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}