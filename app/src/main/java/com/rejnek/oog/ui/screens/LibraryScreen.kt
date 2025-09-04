package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.components.rememberGameFilePicker
import com.rejnek.oog.ui.navigation.Routes
import com.rejnek.oog.ui.viewmodels.LibraryViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onGameStarted: () -> Unit = {},
    viewModel: LibraryViewModel = koinViewModel()
) {
    val libraryGames = viewModel.libraryGames.collectAsState().value

    val launchFilePicker = rememberGameFilePicker { gamePackage ->
        viewModel.onAddGameFromFile(gamePackage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Game Library",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = launchFilePicker
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Add game",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add")
                }
            }
        }
    ) { innerPadding ->
        LibraryScreenContent(
            games = libraryGames,
            onGameSelected = { gameId ->
                viewModel.onGameSelected(gameId, onGameStarted)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LibraryScreenContent(
    games: List<com.rejnek.oog.data.model.GamePackage>,
    onGameSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (games.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No games in library",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Use the + button to add games to your library",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameCard(
                        game = game,
                        onGameSelected = { onGameSelected(game.getId()) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: GamePackage,
    onGameSelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onGameSelected
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = game.getName(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = game.info("description"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
