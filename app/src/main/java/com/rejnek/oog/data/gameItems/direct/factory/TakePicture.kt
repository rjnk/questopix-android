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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.ui.components.permissions.rememberCameraPermissionRequester
import java.io.File

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
                val gameImagesDir = File(context.filesDir, "user_images/$gameId")
                if (!gameImagesDir.exists()) gameImagesDir.mkdirs()
                val tempFile = File(context.cacheDir, "temp_camera_image.jpg")
                try {
                    if (tempFile.exists()) {
                        tempFile.copyTo(imageFile, overwrite = true)
                        capturedImagePath = null
                        capturedImagePath = imageFile.absolutePath
                        tempFile.delete()
                    }
                } catch (_: Exception) { }
            }
        }

        fun launchCameraCapture() {
            val tempFile = File(context.cacheDir, "temp_camera_image.jpg")
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
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo")
                    }
                } else {
                    // Show captured image
                    val bitmap = BitmapFactory.decodeFile(capturedImagePath)
                    bitmap?.let {
                        val isPortrait = it.height > it.width
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Captured photo",
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
                            contentDescription = "Take Photo Again",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Again")
                    }
                }

                // Show permission dialog if needed
                if (permissionRequester.showPermanentDeniedDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Intentionally blank */ },
                        confirmButton = {
                            TextButton(onClick = { permissionRequester.resetPermanentDeniedDialog() }) {
                                Text("OK")
                            }
                        },
                        title = { Text("Camera Permission Required") },
                        text = { Text("Taking photos doesn't work without granting the camera permission. Please grant it in the device settings.") }
                    )
                }
            }
        }
    }
}