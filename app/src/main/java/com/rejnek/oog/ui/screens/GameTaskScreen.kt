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
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameNavigationTextScreen(
    onNextNavigation: () -> Unit,
    onNextTask: () -> Unit,
    onFinishTask: () -> Unit,
    viewModel: GameTaskViewModel = koinViewModel()
) {
    // Focus requester for text field
    val focusRequester = remember { FocusRequester() }

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
    val questionState by viewModel.questionState.collectAsState()
    val answerText by viewModel.answerText.collectAsState()

    // Auto focus on text field when question appears
    LaunchedEffect(questionState) {
        if (questionState != null) {
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Handle potential focus request exceptions
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

            // Display question input if there's an active question
            questionState?.let { question ->
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { viewModel.onAnswerTextChanged(it) },
                    label = { Text("Your answer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.submitAnswer() }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.submitAnswer() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display JavaScript buttons if available and no active question
            if (questionState == null && buttons.isNotEmpty()) {
                buttons.forEachIndexed { index, button ->
                    Button(
                        onClick = { viewModel.onJsButtonClicked(index) }
                    ) {
                        Text(button.text)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            // Show default continue button when no custom buttons or questions are available
            else if (questionState == null && buttons.isEmpty()) {
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
}
