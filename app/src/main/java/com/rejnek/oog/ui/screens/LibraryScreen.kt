package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.components.library.DeleteGamesDialog
import com.rejnek.oog.ui.components.library.DuplicateGameDialog
import com.rejnek.oog.ui.components.library.LibraryScreenContent
import com.rejnek.oog.ui.components.library.LibraryTopBar
import com.rejnek.oog.ui.components.library.rememberGameFilePicker
import com.rejnek.oog.ui.navigation.Routes
import com.rejnek.oog.ui.viewmodels.LibraryViewModel
import com.rejnek.oog.ui.viewmodels.SharedEventsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGameInfo: (String) -> Unit = {},
    sharedEvents: SharedEventsViewModel? = null,
    viewModel: LibraryViewModel = koinViewModel()
) {
    // library games
    val libraryGames = viewModel.libraryGames.collectAsState().value
    // selecting
    val selectedGameIds = viewModel.selectedGameIds.collectAsState().value
    val isSelectionMode = viewModel.isSelectionMode.collectAsState().value
    var showDeleteDialog by remember { mutableStateOf(false) }
    // file picker
    val launchFilePicker = rememberGameFilePicker { viewModel.onAddGameFromFile(it, onNavigateToGameInfo) }
    // dialog for duplicity import
    val showDuplicateDialog = viewModel.showDuplicateDialog.collectAsState().value
    val pendingGamePackage = viewModel.pendingGamePackage.collectAsState().value
    // game in progress state
    val gameIsInProgress by viewModel.gameIsInProgress.collectAsState()

    // Observe shared import event
    val requestImport = sharedEvents?.requestImportGame?.collectAsState()?.value
    LaunchedEffect(requestImport) {
        if (requestImport == true) {
            // consume then launch picker (avoid double triggers)
            sharedEvents.consumeImportGame()
            launchFilePicker()
        }
    }

    DuplicateGameDialog(
        show = showDuplicateDialog,
        gameName = pendingGamePackage?.getName() ?: "",
        onConfirm = { viewModel.onConfirmDuplicateReplace(onNavigateToGameInfo) },
        onCancel = { viewModel.onCancelDuplicateReplace() }
    )

    DeleteGamesDialog(
        show = showDeleteDialog,
        count = selectedGameIds.size,
        onConfirm = {
            viewModel.deleteSelectedGames()
            showDeleteDialog = false
        },
        onDismiss = { showDeleteDialog = false }
    )

    Scaffold(
        topBar = {
            LibraryTopBar(
                isSelectionMode = isSelectionMode,
                selectedCount = selectedGameIds.size,
                totalGames = libraryGames.size,
                onExitSelectionMode = { viewModel.toggleSelectionMode() },
                onEnterSelectionMode = { viewModel.toggleSelectionMode() },
                onSelectAll = { viewModel.selectAllGames() },
                onDeleteSelected = { showDeleteDialog = true },
                onNavigateToSettings = onNavigateToSettings
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.LibraryScreen,
                onNavigate = { route ->
                    when (route) {
                        Routes.HomeScreen -> onNavigateToHome()
                        Routes.LibraryScreen -> { /* no-op */ }
                        else -> {}
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSelectionMode && !gameIsInProgress) {
                FloatingActionButton(onClick = launchFilePicker) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.cd_add_game),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    ) { innerPadding ->
        LibraryScreenContent(
            games = libraryGames,
            selectedGameIds = selectedGameIds,
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
