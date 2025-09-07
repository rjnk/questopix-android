package com.rejnek.oog.ui.screens

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
    LaunchedEffect(finishGame) { if (finishGame) onFinishTask() }

    val gameName by viewModel.gameName.collectAsState()
    val uiElements by viewModel.uiElements.collectAsState()

    BackHandler {  }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TopBar(gameName) { onOpenSettings() }

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme .colorScheme.background)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            uiElements.forEach { element ->
                element()
                Spacer(modifier = Modifier.height(8.dp))
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
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings"
            )
        }
    }
}