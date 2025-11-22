/*
 * Created with Github Copilot
 */
package com.rejnek.oog.ui.components.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Creates a reusable camera permission requester.
 *
 * Exposes current permission status, a request() function, and a flag
 * when the permission is permanently denied.
 *
 * @param onPermissionGranted Callback when permission is granted
 * @param onPermanentlyDenied Callback when permission is permanently denied
 */
@Composable
fun rememberCameraPermissionRequester(
    onPermissionGranted: () -> Unit = {},
    onPermanentlyDenied: () -> Unit = {}
): CameraPermissionRequester {
    val context = LocalContext.current

    val hasPermissionState = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermanentDeniedDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermissionState.value = granted
        if (granted) {
            onPermissionGranted()
        } else {
            // Immediately mark as permanently denied for simplification (no auto retry)
            showPermanentDeniedDialog = true
            onPermanentlyDenied()
        }
    }

    return remember {
        CameraPermissionRequester(
            hasPermissionProvider = { hasPermissionState.value },
            requestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            showPermanentDeniedDialogProvider = { showPermanentDeniedDialog },
            resetPermanentDeniedDialog = { showPermanentDeniedDialog = false }
        )
    }
}

/** State holder for camera permission status and request functionality. */
class CameraPermissionRequester internal constructor(
    private val hasPermissionProvider: () -> Boolean,
    private val requestPermission: () -> Unit,
    private val showPermanentDeniedDialogProvider: () -> Boolean,
    val resetPermanentDeniedDialog: () -> Unit
) {
    val hasPermission: Boolean get() = hasPermissionProvider()
    fun request() = requestPermission()
    val showPermanentDeniedDialog: Boolean get() = showPermanentDeniedDialogProvider()
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
