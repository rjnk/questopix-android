package com.rejnek.oog.data.repository

import android.content.Context
import android.util.Log
import java.io.File

class GameImageRepository {

    fun deleteAllImages(context: Context, packageId: String) {
        val imagesDir = File(context.filesDir, "user_images/$packageId")
        if (imagesDir.exists()) {
            imagesDir.deleteRecursively()
        }
    }
}