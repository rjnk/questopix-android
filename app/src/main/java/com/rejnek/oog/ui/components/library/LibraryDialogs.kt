package com.rejnek.oog.ui.components.library

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.rejnek.oog.data.model.GamePackage

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
        title = { Text("Game Already Exists") },
        text = { Text("A game with the name '$gameName' already exists in your library. Replace it with the new version?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Replace") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
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
        title = { Text("Delete Games") },
        text = {
            Text("Are you sure you want to delete $count game${if (count == 1) "" else "s"}? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

