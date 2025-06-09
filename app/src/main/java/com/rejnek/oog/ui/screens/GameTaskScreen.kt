package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun GameTaskScreenPreview() {
    GameTaskScreen(onContinueClick = {})
}

@Composable
fun GameTaskScreen(
    onContinueClick: () -> Unit,
    viewModel: GameTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var answer by remember { mutableStateOf("") }

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
                Text("Loading task...")
            } else {
                Text(
                    text = uiState.taskTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = uiState.taskDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Show coordinates if available
                uiState.coordinates?.let { coords ->
                    Text(
                        text = "Location: ${coords.lat}, ${coords.lng} (radius: ${coords.radius}m)",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (!uiState.isTaskCompleted) {
                    Button(
                        onClick = {
                            viewModel.onContinueClicked()
                        },
                        enabled = answer.isNotBlank()
                    ) {
                        Text("Submit Answer")
                    }
                } else {
                    // Task completed
                    if (uiState.feedback.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = uiState.feedback,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.onContinueClicked()
                            onContinueClick()
                        }
                    ) {
                        Text("Complete Task")
                    }
                }
            }
        }
    }
}
