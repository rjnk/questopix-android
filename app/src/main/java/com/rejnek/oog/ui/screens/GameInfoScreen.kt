/*
 * Created with Github Copilot
 */
package com.rejnek.oog.ui.screens

import LocationPermissionRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.ui.components.gameInfo.AttributeItem
import com.rejnek.oog.ui.components.gameInfo.GameCoverImage
import com.rejnek.oog.ui.components.gameInfo.InfoSection
import com.rejnek.oog.ui.components.gameInfo.LocationItem
import com.rejnek.oog.ui.viewmodel.GameInfoViewModel
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.androidx.compose.koinViewModel
import android.widget.Toast

/**
 * Screen displaying detailed information about a game package.
 *
 * Shows game description, start/finish locations, cover image, and attributes.
 * Handles location permission requests and game start logic.
 *
 * @param gameId Unique identifier of the game to display
 * @param onNavigateBack Callback for back navigation
 * @param onGameStarted Callback invoked when the game starts successfully
 * @param viewModel ViewModel managing game info state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameInfoScreen(
    gameId: String,
    onNavigateBack: () -> Unit = {},
    onGameStarted: () -> Unit = {},
    viewModel: GameInfoViewModel = koinViewModel()
) {
    val gamePackage = viewModel.gamePackage.collectAsState().value
    val isTaskRequiringLocation = viewModel.isTaskRequiringLocation.collectAsState()
    val locationPermissionGranted = viewModel.locationPermissionGranted.collectAsState()
    val showFarAwayToast = viewModel.showFarAwayToast.collectAsState()
    val showNoLocationToast = viewModel.showNoLocationToast.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(showFarAwayToast.value) {
        if (showFarAwayToast.value) {
            Toast.makeText(
                context,
                context.getString(R.string.toast_too_far),
                Toast.LENGTH_LONG
            ).show()
            viewModel.farAwayToastShown()
        }
    }

    LaunchedEffect(showNoLocationToast.value) {
        if (showNoLocationToast.value) {
            Toast.makeText(
                context,
                context.getString(R.string.toast_no_location),
                Toast.LENGTH_LONG
            ).show()
            viewModel.noLocationToastShown()
        }
    }

    LaunchedEffect(gameId) {
        viewModel.loadGameInfo(gameId, onGameStarted)
    }

    if (isTaskRequiringLocation.value) {
        LocationPermissionRequest(
            locationPermissionGranted = locationPermissionGranted.value,
            onGoToLibrary = onNavigateBack,
            onRefreshLocationPermission = { viewModel.refreshLocationPermission() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = gamePackage?.getName() ?: stringResource(R.string.game_info_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (gamePackage != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.startGameByPressingButton(onGameStarted) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.cd_start_game)
                        )
                    },
                    text = { Text(stringResource(R.string.start_game)) }
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

/**
 * Scrollable content layout for game information.
 *
 * @param gamePackage The game package data to display
 * @param modifier Modifier for the content container
 */
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
            InfoSection(title = stringResource(R.string.description)) {
                Text(
                    text = gamePackage.getDescription(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            // Locations
            InfoSection(title = stringResource(R.string.locations)) {
                LocationItem(
                    title = stringResource(R.string.start_location),
                    locationText = gamePackage.getStartLocationText() ?: stringResource(R.string.unknown),
                    gpsCoordinates = gamePackage.getStartLocation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LocationItem(
                    title = stringResource(R.string.finish_location),
                    locationText = gamePackage.getFinishLocationText() ?: stringResource(R.string.unknown),
                    gpsCoordinates = gamePackage.getFinishLocation()
                )
            }
        }

        // Cover image
        val coverPhoto = gamePackage.getCoverPhoto()
        if (coverPhoto.isNotEmpty()) {
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
                InfoSection(title = stringResource(R.string.game_details)) {
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