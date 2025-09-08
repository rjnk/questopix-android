import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LocationPermissionRequest(
    locationPermissionGranted: Boolean,
    onGoToLibrary: () -> Unit,
    onRefreshLocationPermission: () -> Unit,
){
    // Implemented: request precise location permission when not granted, show denial dialog if refused
    var showDeniedDialog by remember { mutableStateOf(false) }
    var permissionRequestAttempted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onRefreshLocationPermission()
        if (!granted) {
            showDeniedDialog = true
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted && !permissionRequestAttempted) {
            permissionRequestAttempted = true
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showDeniedDialog && !locationPermissionGranted) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = onGoToLibrary) {
                    Text("OK")
                }
            },
            title = { Text("Permission required") },
            text = { Text("The app cannot function without precise location permission. Please enable it in system settings.") }
        )
    }
}
