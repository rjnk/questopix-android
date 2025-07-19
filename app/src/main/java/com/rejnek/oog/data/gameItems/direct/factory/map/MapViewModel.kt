package com.rejnek.oog.data.gameItems.direct.factory.map

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.services.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point

class MapViewModel(
    app: Application,
    val repository: GameRepository
) : AndroidViewModel(app) {
    private val _mapView: MutableStateFlow<MapView?> = MutableStateFlow(null)

    private val _isMapInitialized = MutableStateFlow(false)
    val isMapInitialized: StateFlow<Boolean> = _isMapInitialized

    // Location service
    private val locationService = LocationService(app.applicationContext)

    private var mapLibreMap: MapLibreMap? = null

    // Mapy.com API key
    private val mapyApiKey = "nHQoydgWRXuzTsUY4_GESDAfL00F0QKIeeZp3nmAXg0"

    // Default location (updated to match your example - near Czech Republic center)
    private val defaultLocation = LatLng(50.0, 14.0)
    private val defaultZoom = 15.0

    init {
        // Start observing location updates
        viewModelScope.launch {
            locationService.currentLocation.collect { (latitude, longitude) ->
                if (latitude != 0.0 && longitude != 0.0) {
                    updateLocationOnMap(latitude, longitude)
                }
            }
        }

//        // Observe visible elements changes and update markers
//        viewModelScope.launch {
//            repository.visibleElements.collect {
//                updateGameMarkers()
//            }
//        }
    }

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
            this.mapLibreMap = map // Store reference to the map
            // Disable map rotation
            map.uiSettings.isRotateGesturesEnabled = false
            // Configure custom style with Mapy.com tiles
            configureMapyStyle(map)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))

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
            val tileSet =
                TileSet("2.2.0", tileUrl)  // Fixed: passing a single URL string instead of an array
            tileSet.minZoom = 0f
            tileSet.maxZoom = 20f

            // Create source
            val rasterSource = RasterSource(sourceId, tileSet, 256)

            // Create layer
            val rasterLayer = RasterLayer(layerId, sourceId)

            // Add source and layer to style
            loadedStyle.addSource(rasterSource)
            loadedStyle.addLayer(rasterLayer)

            // Add custom markers after style is loaded
//            updateGameMarkers()
        }
    }

    private fun updateLocationOnMap(latitude: Double, longitude: Double) {
        mapLibreMap?.let { map ->
            val currentLocation = LatLng(latitude, longitude)

            // Update or add the location feature
            val locationPoint = Point.fromLngLat(longitude, latitude)

            map.style?.let { style ->
                val sourceId = "user-location-source"
                val layerId = "user-location-layer"

                if (style.getSource(sourceId) == null) {
                    // Create a new GeoJSON source and layer for user location
                    val geoJsonSource = GeoJsonSource(sourceId, Feature.fromGeometry(locationPoint))
                    style.addSource(geoJsonSource)

                    // Add a circle layer to represent user location
                    val circleLayer = CircleLayer(layerId, sourceId)
                        .withProperties(
                            PropertyFactory.circleRadius(8f),
                            PropertyFactory.circleColor("#007AFF"), // Blue color
                            PropertyFactory.circleStrokeWidth(2f),
                            PropertyFactory.circleStrokeColor("#FFFFFF") // White border
                        )
                    style.addLayer(circleLayer)
                } else {
                    // Update existing source with new location
                    val source = style.getSource(sourceId) as? GeoJsonSource
                    source?.setGeoJson(Feature.fromGeometry(locationPoint))
                }
            }
        }
    }

    fun centerOnUserLocation() {
        locationService.currentLocation.value.let { (latitude, longitude) ->
            mapLibreMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), defaultZoom))
        }
    }

//    private fun updateGameMarkers() {
//        mapLibreMap?.style?.let { style ->
//            // Remove existing game marker layers and sources
//            var index = 0
//            while (style.getLayer("game-marker-layer-$index") != null) {
//                style.removeLayer("game-marker-layer-$index")
//                style.removeSource("game-marker-source-$index")
//                index++
//            }
//
//            // Add new markers from repository
//            val markers = repository.visibleElements.value.mapNotNull { element ->
//                element.coordinates?.let { coords ->
//                    Pair(coords.lat, coords.lng)
//                }
//            }
//
//            markers.forEachIndexed { markerIndex, (latitude, longitude) ->
//                val sourceId = "game-marker-source-$markerIndex"
//                val layerId = "game-marker-layer-$markerIndex"
//
//                // Create point for marker location
//                val markerPoint = Point.fromLngLat(longitude, latitude)
//                val geoJsonSource = GeoJsonSource(sourceId, Feature.fromGeometry(markerPoint))
//
//                // Add source to style
//                style.addSource(geoJsonSource)
//
//                // Create circle layer for marker with distinct styling
//                val circleLayer = CircleLayer(layerId, sourceId)
//                    .withProperties(
//                        PropertyFactory.circleRadius(12f),
//                        PropertyFactory.circleColor("#FF5722"), // Orange-red color
//                        PropertyFactory.circleStrokeWidth(3f),
//                        PropertyFactory.circleStrokeColor("#FFFFFF") // White border
//                    )
//                style.addLayer(circleLayer)
//            }
//        }
//    }

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