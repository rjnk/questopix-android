package com.rejnek.oog.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rejnek.oog.ui.components.GameNavBar

@Composable
fun GameMapScreen(
    onNavigateToMenu: () -> Unit = {},
) {
    var selectedIndex by remember { mutableStateOf(1) }
    Scaffold(
        bottomBar = {
            GameNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    if (index == 0) onNavigateToMenu()
                }
            )
        }
    ) { innerPadding ->
        // This is a placeholder for the game map screen.
        // You can implement the UI and logic for the game map here.

        // Example usage of the parameters:
        // onBackToMenu() to navigate back to the menu
        // onTaskSelected(taskId) to select a task from the map

        // Place your map content here, using Modifier.padding(innerPadding) if needed
    }
}
