package com.rejnek.oog.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rejnek.oog.ui.components.GameNavBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.LocalActivity
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameMapScreen(
    onNavigateToMenu: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use activity as ViewModelStoreOwner to keep MapViewModel alive across navigation
    val activity = LocalActivity.current as? ViewModelStoreOwner
    val mapViewModel: MapViewModel = if (activity != null) {
        koinViewModel(viewModelStoreOwner = activity)
    } else {
        koinViewModel()
    }

    // Get the MapView from ViewModel and initialize if needed
    val mapView = mapViewModel.getOrCreateMapView(context)
    val isMapInitialized by mapViewModel.isMapInitialized.collectAsState()

    LaunchedEffect(Unit) {
        if (!isMapInitialized) {
            mapViewModel.initializeMap(mapView)
        }
    }

    Scaffold(
        bottomBar = {
            GameNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    if (index == 0) onNavigateToMenu()
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { mapView }
        )

        // Handle MapView lifecycle via ViewModel
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> mapViewModel.onStart()
                    Lifecycle.Event.ON_RESUME -> mapViewModel.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapViewModel.onPause()
                    Lifecycle.Event.ON_STOP -> mapViewModel.onStop()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}
