package com.rejnek.oog.engine.commands.direct.factory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.padding
import com.rejnek.oog.engine.commands.GenericDirectFactory
import java.io.File

// usage: image("example.png");
class ImageFactory : GenericDirectFactory() {
    override val id = "image"

    override suspend fun create(data: String) {
        Log.d("ImageFactory", "Creating image with data: $data")
        gameRepository?.addUIElement {
            MyImage(
                gameId = gameRepository?.currentGamePackage?.value?.getId() ?: throw Exception("Game ID not found"),
                filename = data
            ).Show()
        }
    }
}

class MyImage(
    private val gameId: String,
    private val filename: String
) {
    @Composable
    fun Show() {
        val context = LocalContext.current
        val imageFile = File(context.filesDir, "game_images/$gameId/$filename")

        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            bitmap?.let {
                val isPortrait = it.height > it.width
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = filename,
                    modifier = if (isPortrait) {
                        Modifier.width(250.dp).padding(vertical = 8.dp)
                    } else {
                        Modifier.padding(vertical = 8.dp)
                    },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
