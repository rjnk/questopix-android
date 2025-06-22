package com.rejnek.oog.ui.screens

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory
import android.widget.LinearLayout
import android.view.Gravity
import android.view.View
import android.content.Intent
import android.net.Uri

class MapViewModel(app: Application) : AndroidViewModel(app) {
    private val _mapView: MutableStateFlow<MapView?> = MutableStateFlow(null)
    val mapView: StateFlow<MapView?> = _mapView

    private val _isMapInitialized = MutableStateFlow(false)
    val isMapInitialized: StateFlow<Boolean> = _isMapInitialized

    // Mapy.com API key
    private val mapyApiKey = "nHQoydgWRXuzTsUY4_GESDAfL00F0QKIeeZp3nmAXg0"

    // Default location (updated to match your example - near Czech Republic center)
    private val defaultLocation = LatLng(49.8729317, 14.8981184)
    private val defaultZoom = 15.0

    fun getOrCreateMapView(context: Context): MapView {
        _mapView.value?.let { return it }

        // Initialize MapLibre before creating MapView
        MapLibre.getInstance(context, mapyApiKey, WellKnownTileServer.MapLibre)
        return MapView(context).also { _mapView.value = it }
    }

    fun initializeMap(mapView: MapView) {
        if (_isMapInitialized.value) return

        mapView.onCreate(null)
        mapView.getMapAsync { map ->
            // Configure custom style with Mapy.com tiles
            configureMapyStyle(map)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))

            // Add the required Mapy.com logo
            addMapyLogo(mapView.context, map)

            _isMapInitialized.value = true
        }
    }

    private fun configureMapyStyle(map: MapLibreMap) {
        // Use the Style.Builder API to programmatically create the style
        val sourceId = "mapy-source"
        val layerId = "mapy-layer"

        // Create a new style from scratch
        val style = Style.Builder()

        // Set up the style once it's loaded
        map.setStyle(style) { loadedStyle ->
            // Create a tile set with Mapy.com URL
            val tileUrl = "https://api.mapy.com/v1/maptiles/basic/256/{z}/{x}/{y}?apikey=$mapyApiKey"
            val tileSet = TileSet("2.2.0", tileUrl)  // Fixed: passing a single URL string instead of an array
            tileSet.minZoom = 0f
            tileSet.maxZoom = 20f

            // Create source
            val rasterSource = RasterSource(sourceId, tileSet, 256)

            // Create layer
            val rasterLayer = RasterLayer(layerId, sourceId)

            // Add source and layer to style
            loadedStyle.addSource(rasterSource)
            loadedStyle.addLayer(rasterLayer)
        }
    }

    private fun addMapyLogo(context: Context, map: MapLibreMap) {
        val logoView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            val logoImage = ImageView(context).apply {
                // We'd need to add the Mapy.com logo to the project's resources
                // For now, we're creating a placeholder
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // You should replace this with the actual Mapy.com logo
                // setImageResource(R.drawable.mapy_logo)
            }

            setOnClickListener {
                // Open Mapy.com when logo is clicked
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mapy.com/"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            addView(logoImage)
        }

        // Add the logo view to the map (this is simplified - in a real implementation
        // you would need to create a proper custom control similar to the JavaScript example)
        // mapView.addView(logoView)
    }

    // Lifecycle methods to be called from the UI
    fun onStart() {
        _mapView.value?.onStart()
    }

    fun onResume() {
        _mapView.value?.onResume()
    }

    fun onPause() {
        _mapView.value?.onPause()
    }

    fun onStop() {
        _mapView.value?.onStop()
    }

    override fun onCleared() {
        _mapView.value?.let { mapView ->
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
        _mapView.value = null
        _isMapInitialized.value = false
        super.onCleared()
    }
}
