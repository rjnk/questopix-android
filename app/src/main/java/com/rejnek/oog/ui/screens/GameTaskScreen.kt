package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameTaskScreen(
    onFinishTask: () -> Unit,
    viewModel: GameTaskViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.Finish -> onFinishTask()
            }
        }
    }

    // Collect the UI state
    val uiElements by viewModel.uiElements.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Render dynamically added UI elements
            uiElements.forEach { element ->
                element()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
