package com.rejnek.oog.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.rejnek.oog.ui.components.GameNavBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import com.rejnek.oog.data.gameItems.direct.map.GameMap

@Composable
fun GameMapScreen(
    onNavigateToMenu: () -> Unit = {}
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
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GameMap().Show()
        }
    }
}
