package com.rejnek.oog.data.gameItems.direct.factory

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.repository.LocalCaptureMode
import java.io.File

class ShowAllImagesFactory : GenericDirectFactory() {
    override val id = "showAllImages"

    override suspend fun create(data: String) {
        val gamePackage = gameRepository?.currentGamePackage?.value ?: return

        gameRepository?.addUIElement {
            MyImageGallery(
                gameId = gamePackage.getId(),
                heading = if(data == "") "Photos" else data
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
                    text = "No images taken yet",
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
                Text(
                    text = heading,
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
                    // Swipeable image gallery - full width
                    HorizontalPager(
                        state = pagerState!!,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val imageFile = imageFiles[page]
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Taken image ${page + 1}",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }

                    // Page indicators
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
                                            containerColor = if (index == pagerState.currentPage) {
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
