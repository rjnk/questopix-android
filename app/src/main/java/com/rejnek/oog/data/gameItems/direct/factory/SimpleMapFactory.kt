package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SimpleMapData(
    val backgroundImage: String,
    val topLeftLat: Double,
    val topLeftLng: Double,
    val bottomRightLat: Double,
    val bottomRightLng: Double
)

// usage: simpleMap('{"backgroundImage":"map1.png","topLeftLat":50.114283,"topLeftLng":14.388549,"bottomRightLat":50.107761,"bottomRightLng":14.399235}');
class SimpleMapFactory : GenericDirectFactory() {
    override val id = "simpleMap"

    override suspend fun create(data: String, callbackId: String) {
        val mapData = Json.decodeFromString<SimpleMapData>(data)
        val simpleMap = SimpleMap(
            gameId = gameRepository?.currentGamePackage?.value?.getId() ?: "",
            mapData = mapData,
            gameRepository = gameRepository
        )
        gameRepository?.addUIElement {
            simpleMap.Show()
        }
    }
}

class SimpleMap(
    private val gameId: String,
    private val mapData: SimpleMapData,
    private val gameRepository: com.rejnek.oog.data.repository.GameRepository?
) {
    @Composable
    fun Show() {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val imageFile = File(context.filesDir, "game_images/$gameId/${mapData.backgroundImage}")
        val userLocation = gameRepository?.gameLocationRepository?.currentLocation?.collectAsState()?.value

        // Create pulsing animation for user location
        val infiniteTransition = rememberInfiniteTransition()
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .offset(x = (-16).dp) // Shift left to counteract parent padding
                .width(screenWidth) // Use full screen width
        ) {
            // Background image
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Map background",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                // User location overlay - positioned exactly over the image
                Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(it.width.toFloat() / it.height.toFloat())) {
                    userLocation?.let { location ->
                        drawUserLocation(location.lat, location.lng, size.width, size.height, pulseScale, pulseAlpha)
                    }
                }
            }
        }
    }

    private fun DrawScope.drawUserLocation(lat: Double, lng: Double, width: Float, height: Float, pulseScale: Float, pulseAlpha: Float) {
        val latRange = mapData.topLeftLat - mapData.bottomRightLat
        val lngRange = mapData.bottomRightLng - mapData.topLeftLng

        val x = (((lng - mapData.topLeftLng) / lngRange) * width).toFloat()
        val y = (((mapData.topLeftLat - lat) / latRange) * height).toFloat()

        val center = androidx.compose.ui.geometry.Offset(x, y)

        // Google Maps standard blue color
        val googleBlue = Color(0xFF1A73E8)

        // Draw outer pulsing circle (like radar effect) - larger
        drawCircle(
            color = googleBlue.copy(alpha = pulseAlpha * 0.3f),
            radius = 48f * pulseScale,
            center = center
        )

        // Draw middle pulsing circle - larger
        drawCircle(
            color = googleBlue.copy(alpha = pulseAlpha * 0.5f),
            radius = 38f * pulseScale,
            center = center
        )

        // Draw main location indicator (larger and more visible)
        drawCircle(
            color = googleBlue,
            radius = 26f,
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = 21f,
            center = center
        )
        drawCircle(
            color = googleBlue,
            radius = 15f,
            center = center
        )
    }
}