package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.LibraryScreen,
                onNavigate = { route ->
                    when (route) {
                        Routes.HomeScreen -> onNavigateToHome()
                        Routes.LibraryScreen -> { /* Already on Library, no action needed */ }
                        else -> { /* Handle other routes if needed */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        LibraryScreenContent(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LibraryScreenContent(
    modifier: Modifier = Modifier
) {
    val gameLibrary = listOf(
        "Adventure Quest",
        "Mystery Manor",
        "Forest Explorer",
        "City Hunt",
        "Treasure Island"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Game Library",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gameLibrary) { gameName ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO: Handle game selection */ }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = gameName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Saved Games",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "No saved games yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
