package com.rejnek.oog.data.repository

import android.content.Context
import android.util.Log
import java.io.File

class GameImageRepository {
    // TODO delete all images when we delete a game from library
    fun deleteAllImages(context: Context, packageId: String) {
        val imagesDir = File(context.filesDir, "user_images/$packageId")
        if (imagesDir.exists()) {
            imagesDir.deleteRecursively()
        }
    }
}