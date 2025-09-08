package com.rejnek.oog.ui.screens

import LocationPermissionRequest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTaskScreen(
    onFinishTask: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: GameTaskViewModel = koinViewModel()
) {
    val finishGame by viewModel.finishGame.collectAsState(initial = false)
    val locationPermissionGranted = viewModel.locationPermissionNeeded.collectAsState(true)
    val gameName by viewModel.gameName.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val uiElements by viewModel.uiElements.collectAsState()

    LaunchedEffect(finishGame) { if (finishGame) onFinishTask() }

    BackHandler { }

    LocationPermissionRequest(
        locationPermissionGranted = locationPermissionGranted.value,
        onGoToLibrary = onFinishTask,
        onRefreshLocationPermission = { viewModel.refreshLocationPermission() }
    )

    Column(Modifier.fillMaxSize()) {
        TopBar(gameName, onOpenSettings)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                uiElements.forEachIndexed { index, element ->
                    element()
                    if (index != uiElements.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = 22.dp, start = 16.dp, bottom = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings"
            )
        }
    }
}