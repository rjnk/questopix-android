package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.ui.viewmodels.GameNavigationTextViewModel
import com.rejnek.oog.ui.viewmodels.GameNavigationTextViewModel.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameNavigationTextScreen(
    onNextNavigation: () -> Unit,
    onNextTask: () -> Unit,
    onFinishTask: () -> Unit,
    viewModel: GameNavigationTextViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.Finish -> onFinishTask()
                // Add other navigation events as needed
            }
        }
    }

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

            Button(
                onClick = {
                    viewModel.onContinueClicked()
                }
            ) {
                Text("Continue to Task")
            }
        }
    }
}
