package com.rejnek.oog.data.gameItems.direct.map

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rejnek.oog.ui.components.GameNavBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import org.koin.androidx.compose.koinViewModel

class MapFactory : GenericDirectFactory() {
    override val id = "map"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement {
            GameMap().Show()
        }
    }
}

class GameMap() {
     @Composable
     fun Show() {
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

         Box(modifier = Modifier.fillMaxSize()) {
             AndroidView(
                 modifier = Modifier.fillMaxSize(),
                 factory = { mapView }
             )

             FloatingActionButton(
                 onClick = { mapViewModel.centerOnUserLocation() },
                 modifier = Modifier
                     .align(Alignment.BottomEnd)
                     .padding(16.dp)
             ) {
                 Icon(
                     imageVector = Icons.Default.MyLocation,
                     contentDescription = "Center on my location"
                 )
             }
         }

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