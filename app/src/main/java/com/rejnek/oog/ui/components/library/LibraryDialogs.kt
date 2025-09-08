package com.rejnek.oog.ui.components.library

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.rejnek.oog.R

@Composable
fun DuplicateGameDialog(
    show: Boolean,
    gameName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.duplicate_game_title)) },
        text = { Text(stringResource(R.string.duplicate_game_message, gameName)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.replace)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun DeleteGamesDialog(
    show: Boolean,
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_games_title)) },
        text = {
            Text(pluralStringResource(R.plurals.delete_games_message, count, count))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
