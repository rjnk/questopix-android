package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rejnek.oog.ui.components.GameNavBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.viewmodels.SecondaryTabViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecondaryTabScreen(
    onNavigateToMenu: () -> Unit = {},
    viewModel: SecondaryTabViewModel = koinViewModel()
) {
    var selectedIndex by remember { mutableStateOf(1) }
    val uiElements by viewModel.uiElements.collectAsState()

    Scaffold(
        bottomBar = {
            GameNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    if (index == 0) onNavigateToMenu()
                }
            )
        },
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
