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
import com.rejnek.oog.ui.viewmodels.GameNavigationTextViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun GameNavigationTextScreenPreview() {
    GameNavigationTextScreen(onContinueClick = {})
}

@Composable
fun GameNavigationTextScreen(
    onContinueClick: () -> Unit,
    viewModel: GameNavigationTextViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onStart()
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading navigation...")
            } else {
                Text(
                    text = uiState.navigationTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = uiState.navigationText,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                uiState.coordinates?.let { coords ->
                    Text(
                        text = "Target: ${coords.lat}, ${coords.lng} (radius: ${coords.radius}m)",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (uiState.nextLocationName.isNotEmpty()) {
                    Text(
                        text = "Next: ${uiState.nextLocationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        viewModel.onContinueClicked()
                        onContinueClick()
                    }
                ) {
                    Text("Continue to Task")
                }
            }
        }
    }
}
