package com.rejnek.oog.engine.commands.direct.factory

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rejnek.oog.R
import com.rejnek.oog.engine.commands.GenericDirectFactory
import com.rejnek.oog.data.repository.LocalCaptureMode
import java.io.File

class ShowAllImagesFactory : GenericDirectFactory() {
    override val id = "showAllImages"

    override suspend fun create(data: String) {
        val gamePackage = gameRepository?.currentGamePackage?.value ?: return

        gameRepository?.addUIElement {
            MyImageGallery(
                gameId = gamePackage.getId(),
                heading = data
            ).Show()
        }
    }
}

class MyImageGallery(
    private val gameId: String,
    private val heading: String
) {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Show() {
        val context = LocalContext.current
        val captureMode = LocalCaptureMode.current
        val gameImagesDir = File(context.filesDir, "user_images/$gameId")

        val imageFiles = remember {
            if (gameImagesDir.exists()) {
                gameImagesDir.listFiles { file ->
                    file.name.startsWith("taken-image-") && file.name.endsWith(".jpg")
                }?.toList() ?: emptyList()
            } else {
                emptyList()
            }
        }

        if (imageFiles.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_images_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
            return
        }

        val pagerState = if (!captureMode) rememberPagerState(pageCount = { imageFiles.size }) else null

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heading
                val headingText = if (heading.isEmpty()) stringResource(R.string.photos) else heading
                Text(
                    text = headingText,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )

                if (captureMode) {
                    // Show all images sequentially for full screenshot capture
                    imageFiles.forEach { imageFile ->
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = imageFile.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                } else {
                    val ps = pagerState!!

                    BoxWithConstraints {
                        val densityInner = LocalDensity.current
                        val availableWidthPx = with(densityInner) { this@BoxWithConstraints.maxWidth.toPx() }
                        val screenHeightPxInner = context.resources.displayMetrics.heightPixels

                        val maxImageHeightDpFromImages = remember(imageFiles.map { it.absolutePath }, availableWidthPx, screenHeightPxInner) {
                            var maxPx = 0f
                            val screenLimitPx = screenHeightPxInner * 0.75f

                            imageFiles.forEach { file ->
                                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                BitmapFactory.decodeFile(file.absolutePath, opts)
                                if (opts.outWidth > 0 && opts.outHeight > 0) {
                                    val desiredHeightPx = availableWidthPx * (opts.outHeight.toFloat() / opts.outWidth.toFloat())
                                    val clamped = desiredHeightPx.coerceAtMost(screenLimitPx)
                                    if (clamped > maxPx) maxPx = clamped
                                }
                            }

                            with(densityInner) { if (maxPx == 0f) (screenHeightPxInner * 0.5f).toDp() else maxPx.toDp() }
                        }

                        HorizontalPager(
                            state = ps,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(maxImageHeightDpFromImages)
                        ) { page ->
                            val imageFile = imageFiles.getOrNull(page) ?: return@HorizontalPager
                            val request = remember(imageFile.absolutePath) {
                                ImageRequest.Builder(context)
                                    .data(imageFile)
                                    .size(availableWidthPx.toInt()) // optional: request decode sized to actual available width
                                    .crossfade(true)
                                    .build()
                            }

                            AsyncImage(
                                model = request,
                                contentDescription = stringResource(R.string.cd_taken_image, page + 1),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Page indicators (use non-null ps)
                    if (imageFiles.size > 1) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(imageFiles.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .padding(2.dp)
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(50),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (index == ps.currentPage) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline
                                            }
                                        ),
                                        modifier = Modifier.size(4.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}