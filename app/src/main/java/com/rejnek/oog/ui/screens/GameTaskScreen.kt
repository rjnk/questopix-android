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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = gameName,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.cd_settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.surfaceContainer)
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
