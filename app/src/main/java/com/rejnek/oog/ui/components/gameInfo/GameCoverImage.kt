package com.rejnek.oog.ui.components.gameInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import java.io.File

@Composable
fun GameCoverImage(
    gameId: String,
    filename: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageFile = File(context.filesDir, "game_images/$gameId/$filename")

    if (imageFile.exists()) {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        bitmap?.let {
            Card(
                modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Game cover image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
