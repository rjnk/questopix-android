package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.engine.gameItems.Question
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel.NavigationEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameNavigationTextScreen(
    onNextNavigation: () -> Unit,
    onNextTask: () -> Unit,
    onFinishTask: () -> Unit,
    viewModel: GameTaskViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.Finish -> onFinishTask()
                // Add other navigation events as needed
            }
        }
    }

    // Collect the UI state
    val buttons by viewModel.buttons.collectAsState()
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
            Text(
                text = viewModel.name.collectAsState().value,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = viewModel.description.collectAsState().value,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Render dynamically added UI elements
            uiElements.forEach { element ->
                element()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
