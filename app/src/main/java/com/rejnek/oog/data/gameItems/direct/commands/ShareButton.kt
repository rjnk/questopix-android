package com.rejnek.oog.data.gameItems.direct.commands

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ShareButtonFactory() : GenericDirectFactory() {
    override val id: String = "shareButton"

    override suspend fun create(data: String) {

        gameRepository?.addUIElement {
            ShareButton( {
                // take screenshot of the whole screen (INCLUDING scrolling)\
                // open share dialog with the screenshot
            } ).Show()
        }
    }
}

class ShareButton(
    onClick: () -> Unit
) {
    private val _onButtonClick = MutableStateFlow<() -> Unit>(onClick)
    val onButtonClick = _onButtonClick.asStateFlow()

    @Composable
    fun Show(
        modifier: Modifier = Modifier
    ) {
        val text = "Share the results!"
        val onClick by onButtonClick.collectAsState()

        // Generate a random color from 3 Material theme colors that look good on light green
        val randomColorIndex = remember {
            Random.nextInt(3)
        }

        val selectedColor = when (randomColorIndex) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.tertiary
        }

        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Icon",
                modifier = Modifier
                    .padding(end = 8.dp)
            )
            Text(text = text)
        }
    }
}











