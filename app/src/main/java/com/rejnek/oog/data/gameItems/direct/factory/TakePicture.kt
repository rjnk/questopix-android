package com.rejnek.oog.data.gameItems.direct.factory

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.FileOutputStream
import com.rejnek.oog.R
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.ui.components.permissions.rememberCameraPermissionRequester
import java.io.File
import androidx.core.graphics.scale

class TakePictureFactory : GenericDirectFactory() {
    override val id = "takePicture"

    override suspend fun create(data: String) {
        Log.d("TakePictureFactory", "Creating take picture with data: $data")
        val gamePackage = gameRepository?.currentGamePackage?.value ?: return

        gameRepository?.addUIElement {
            MyTakePicture(
                gameId = gamePackage.getId(),
                prompt = data,
                currentTask = gamePackage.currentTaskId
            ).Show()
        }
    }
}

// Note: this is heavily Copilot generated and the code is quite verbose. As this is a self contained module, it's fine for now.
class MyTakePicture(
    private val gameId: String,
    private val prompt: String,
    private val currentTask: String
) {
    @Composable
    fun Show() {
        val context = LocalContext.current
        val fileName = "taken-image-$currentTask.jpg"
        val imageFile = remember { File(context.filesDir, "user_images/$gameId/$fileName") }

        var capturedImagePath by remember { mutableStateOf(if (imageFile.exists()) imageFile.absolutePath else null) }
        var tempImageUri by remember { mutableStateOf<Uri?>(null) }
        var pendingCapture by remember { mutableStateOf(false) }

        // Camera launcher
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && tempImageUri != null) {
                // Ensure destination directory exists
                val gameImagesDir = File(context.filesDir, "user_images/$gameId")
                if (!gameImagesDir.exists()) gameImagesDir.mkdirs()
                val tempFile = File(context.cacheDir, "temp_camera_image.jpg")
                try {
                    if (tempFile.exists()) {
                        // Decode bitmap with high quality config
                        val options = BitmapFactory.Options().apply {
                            inPreferredConfig = Bitmap.Config.ARGB_8888
                        }
                        val bitmap: Bitmap? = BitmapFactory.decodeFile(tempFile.absolutePath, options)

                        // If bitmap was decoded, correct orientation via EXIF and save final image
                        if (bitmap != null) {
                            try {
                                val exif = ExifInterface(tempFile.absolutePath)
                                val orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL
                                )
                                val matrix = Matrix()
                                when (orientation) {
                                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                                        matrix.postRotate(90f)
                                        matrix.preScale(-1f, 1f)
                                    }
                                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                                        matrix.postRotate(270f)
                                        matrix.preScale(-1f, 1f)
                                    }
                                    else -> { /* no-op */ }
                                }

                                val rotatedBitmap = if (!matrix.isIdentity) {
                                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                                } else bitmap

                                // Optionally scale down if extremely large to avoid OOM, keep reasonable max dimension
                                val maxDim = 3840 // allow high resolution but limit extreme sizes
                                val finalBitmap = if (rotatedBitmap.width > maxDim || rotatedBitmap.height > maxDim) {
                                    val scale = maxDim.toFloat() / maxOf(rotatedBitmap.width, rotatedBitmap.height)
                                    rotatedBitmap.scale(
                                        (rotatedBitmap.width * scale).toInt(),
                                        (rotatedBitmap.height * scale).toInt()
                                    )
                                } else rotatedBitmap

                                // Save to final image file with good quality
                                FileOutputStream(imageFile).use { out ->
                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                }

                                // Recycle bitmaps if different instances
                                if (finalBitmap !== rotatedBitmap && rotatedBitmap != bitmap) rotatedBitmap.recycle()
                                if (rotatedBitmap !== bitmap) bitmap.recycle()

                                capturedImagePath = null
                                capturedImagePath = imageFile.absolutePath
                            } catch (_: Exception) {
                                // Fallback: copy raw file
                                try { tempFile.copyTo(imageFile, overwrite = true); capturedImagePath = imageFile.absolutePath } catch (_: Exception) { }
                            }
                        } else {
                            // If decoding failed, fallback to raw copy
                            try { tempFile.copyTo(imageFile, overwrite = true); capturedImagePath = imageFile.absolutePath } catch (_: Exception) { }
                        }

                        // Cleanup temp file
                        try { tempFile.delete() } catch (_: Exception) { }
                    }
                } catch (_: Exception) { }
            }
        }

        fun launchCameraCapture() {
            // Ensure temp file exists and is writable before launching camera
            val tempFile = File(context.cacheDir, "temp_camera_image.jpg")
            try {
                tempFile.parentFile?.mkdirs()
                if (tempFile.exists()) tempFile.delete()
                tempFile.createNewFile()
            } catch (_: Exception) { }

            tempImageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            tempImageUri?.let { cameraLauncher.launch(it) }
        }

        val permissionRequester = rememberCameraPermissionRequester(
            onPermissionGranted = {
                if (pendingCapture) {
                    launchCameraCapture()
                    pendingCapture = false
                }
            },
            onPermanentlyDenied = { /* no-op */ }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Prompt text
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Camera button or captured image
                if (capturedImagePath == null) {
                    Button(
                        onClick = {
                            if (permissionRequester.hasPermission) {
                                launchCameraCapture()
                            } else {
                                pendingCapture = true
                                permissionRequester.request()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.cd_take_photo),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.take_photo))
                    }
                } else {
                    // Show captured image
                    val bitmap = BitmapFactory.decodeFile(capturedImagePath)
                    bitmap?.let {
                        val isPortrait = it.height > it.width
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = stringResource(R.string.cd_captured_photo),
                            modifier = if (isPortrait) {
                                Modifier.width(200.dp)
                            } else {
                                Modifier.fillMaxWidth()
                            },
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (permissionRequester.hasPermission) {
                                launchCameraCapture()
                            } else {
                                pendingCapture = true
                                permissionRequester.request()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.cd_take_photo_again),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.take_again))
                    }
                }

                // Show permission dialog if needed
                if (permissionRequester.showPermanentDeniedDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Intentionally blank */ },
                        confirmButton = {
                            TextButton(onClick = { permissionRequester.resetPermanentDeniedDialog() }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        title = { Text(stringResource(R.string.camera_permission_required_title)) },
                        text = { Text(stringResource(R.string.camera_permission_required_message)) }
                    )
                }
            }
        }
    }
}