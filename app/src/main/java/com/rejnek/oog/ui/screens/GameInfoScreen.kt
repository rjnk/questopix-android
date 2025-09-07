package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.ui.components.AttributeItem
import com.rejnek.oog.ui.components.GameCoverImage
import com.rejnek.oog.ui.components.InfoSection
import com.rejnek.oog.ui.components.LocationItem
import com.rejnek.oog.ui.viewmodels.GameInfoViewModel
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameInfoScreen(
    gameId: String,
    onNavigateBack: () -> Unit = {},
    onGameStarted: () -> Unit = {},
    viewModel: GameInfoViewModel = koinViewModel()
) {
    val gamePackage = viewModel.gamePackage.collectAsState().value

    LaunchedEffect(gameId) {
        viewModel.loadGameInfo(gameId, onGameStarted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = gamePackage?.getName() ?: "Game Info",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (gamePackage != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.startGame(onGameStarted) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start game"
                        )
                    },
                    text = { Text("Start Game") }
                )
            }
        }
    ) { innerPadding ->
        if(gamePackage != null) {
            GameInfoContent(
                gamePackage = gamePackage,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GameInfoContent(
    gamePackage: GamePackage,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp) // space for FAB
    ) {
        item {
            // Description
            InfoSection(title = "Description") {
                Text(
                    text = gamePackage.info("description"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            // Locations
            InfoSection(title = "Locations") {
                val startLocation = gamePackage.gameInfo["startLocation"]?.jsonObject
                val finishLocation = gamePackage.gameInfo["finishLocation"]?.jsonObject

                startLocation?.let {
                    LocationItem(
                        title = "Start Location",
                        locationText = it["text"]?.jsonPrimitive?.content ?: "Unknown"
                    )
                    if (finishLocation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                finishLocation?.let {
                    LocationItem(
                        title = "Finish Location",
                        locationText = it["text"]?.jsonPrimitive?.content ?: "Unknown"
                    )
                }
            }
        }

        // Cover image
        val coverPhoto = gamePackage.info("coverPhoto")
        if (coverPhoto != "ERROR" && coverPhoto.isNotEmpty()) {
            item {
                GameCoverImage(
                    gameId = gamePackage.getId(),
                    filename = coverPhoto
                )
            }
        }

        // Game attributes
        gamePackage.gameInfo["attributes"]?.jsonObject?.let { attributes ->
            item {
                InfoSection(title = "Game Details") {
                    attributes.forEach { (key, value) ->
                        AttributeItem(
                            label = key,
                            value = value.jsonPrimitive.content
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}