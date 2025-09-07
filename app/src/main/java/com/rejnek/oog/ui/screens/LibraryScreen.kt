package com.rejnek.oog.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.components.library.rememberGameFilePicker
import com.rejnek.oog.ui.navigation.Routes
import com.rejnek.oog.ui.viewmodels.LibraryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGameInfo: (String) -> Unit = {},
    viewModel: LibraryViewModel = koinViewModel()
) {
    val libraryGames = viewModel.libraryGames.collectAsState().value
    val selectedGameIds = viewModel.selectedGameIds.collectAsState().value
    val isSelectionMode = viewModel.isSelectionMode.collectAsState().value
    val showDuplicateDialog = viewModel.showDuplicateDialog.collectAsState().value
    val pendingGamePackage = viewModel.pendingGamePackage.collectAsState().value

    // Local UI state for delete dialog
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf(false) }

    val launchFilePicker = rememberGameFilePicker { gamePackage ->
        viewModel.onAddGameFromFile(gamePackage)
    }

    // Duplicate confirmation dialog
    if (showDuplicateDialog && pendingGamePackage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelDuplicateReplace() },
            title = { Text("Game Already Exists") },
            text = {
                Text("A game with the name '${pendingGamePackage.getName()}' already exists in your library. Do you want to replace it with the new version?")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onConfirmDuplicateReplace() }
                ) {
                    Text("Replace")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onCancelDuplicateReplace() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { setShowDeleteDialog(false) },
            title = { Text("Delete Games") },
            text = {
                val count = selectedGameIds.size
                Text("Are you sure you want to delete $count game${if (count == 1) "" else "s"}? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelectedGames()
                        setShowDeleteDialog(false)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { setShowDeleteDialog(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSelectionMode) "${selectedGameIds.size} selected" else "Game Library",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = if (isSelectionMode) {
                    {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit selection mode"
                            )
                        }
                    }
                } else {
                    {}
                },
                actions = {
                    if (isSelectionMode) {
                        if (selectedGameIds.size < libraryGames.size) {
                            IconButton(onClick = { viewModel.selectAllGames() }) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = "Select all"
                                )
                            }
                        }
                        if (selectedGameIds.isNotEmpty()) {
                            IconButton(onClick = { setShowDeleteDialog(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete selected"
                                )
                            }
                        }
                    } else {
                        if (libraryGames.isNotEmpty()) {
                            IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Select games"
                                )
                            }
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
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
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = launchFilePicker
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add game",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add")
                    }
                }
            }
        }
    ) { innerPadding ->
        LibraryScreenContent(
            games = libraryGames,
            selectedGameIds = selectedGameIds,
            isSelectionMode = isSelectionMode,
            onGameSelected = { gameId ->
                if (isSelectionMode) {
                    viewModel.toggleGameSelection(gameId)
                } else {
                    onNavigateToGameInfo(gameId)
                }
            },
            onGameLongPress = { gameId ->
                if (!isSelectionMode) {
                    viewModel.toggleSelectionMode()
                    viewModel.toggleGameSelection(gameId)
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LibraryScreenContent(
    games: List<com.rejnek.oog.data.model.GamePackage>,
    selectedGameIds: Set<String>,
    isSelectionMode: Boolean,
    onGameSelected: (String) -> Unit,
    onGameLongPress: (String) -> Unit,
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
            // Separate games by completion status
            val unfinishedGames = games.filter { it.state != com.rejnek.oog.data.model.GameState.COMPLETED }
            val completedGames = games.filter { it.state == com.rejnek.oog.data.model.GameState.COMPLETED }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Show unfinished games first
                items(unfinishedGames) { game ->
                    GameCard(
                        game = game,
                        isSelected = game.getId() in selectedGameIds,
                        isSelectionMode = isSelectionMode,
                        onGameSelected = { onGameSelected(game.getId()) },
                        onGameLongPress = { onGameLongPress(game.getId()) }
                    )
                }

                // Show completed games section if there are any
                if (completedGames.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Completed Games",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(completedGames) { game ->
                        GameCard(
                            game = game,
                            isSelected = game.getId() in selectedGameIds,
                            isSelectionMode = isSelectionMode,
                            onGameSelected = { onGameSelected(game.getId()) },
                            onGameLongPress = { onGameLongPress(game.getId()) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GameCard(
    game: GamePackage,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onGameSelected: () -> Unit,
    onGameLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onGameSelected,
                onLongClick = onGameLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        )
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
