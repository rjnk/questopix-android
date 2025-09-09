package com.rejnek.oog.ui.components.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rejnek.oog.R
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    totalGames: Int,
    onExitSelectionMode: () -> Unit,
    onEnterSelectionMode: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (isSelectionMode) stringResource(R.string.selected_count, selectedCount) else stringResource(R.string.game_library),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(onClick = onExitSelectionMode) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_exit_selection_mode)
                    )
                }
            }
        },
        actions = {
            if (isSelectionMode) {
                if (selectedCount < totalGames) {
                    IconButton(onClick = onSelectAll) {
                        Icon(
                            imageVector = Icons.Default.SelectAll,
                            contentDescription = stringResource(R.string.cd_select_all)
                        )
                    }
                }
                if (selectedCount > 0) {
                    IconButton(onClick = onDeleteSelected) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.cd_delete_selected)
                        )
                    }
                }
            } else {
                if (totalGames > 0) {
                    IconButton(onClick = onEnterSelectionMode) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.cd_select_games)
                        )
                    }
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.cd_settings)
                    )
                }
            }
        }
    )
}
