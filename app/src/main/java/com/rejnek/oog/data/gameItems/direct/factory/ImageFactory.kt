package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import android.util.Log
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import java.io.File

class ImageFactory : GenericDirectFactory() {
    override val id = "image"

    override suspend fun create(data: String, callbackId: String) {
        Log.d("ImageFactory", "Creating image with data: $data")
        gameRepository?.addUIElement { MyImage(data).Show() }
    }
}

class MyImage(
    private val filename: String
) {
    @Composable
    fun Show() {
        val context = LocalContext.current
        val imageFile = File(context.filesDir, "game_images/hra/$filename") // TODO read gameId from js

        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = filename
                )
            }
        }
    }
}

