package com.rejnek.oog.ui.components.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GameCard(
    game: GamePackage,
    isSelected: Boolean,
    onGameSelected: () -> Unit,
    onGameLongPress: () -> Unit,
    isEnabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = isEnabled,
                onClick = onGameSelected,
                onLongClick = onGameLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.tertiaryContainer
                game.state == GameState.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                game.state == GameState.ARCHIVED -> MaterialTheme.colorScheme.surfaceContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
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
                fontWeight = FontWeight.Medium,
                color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = game.getDescription(),
                style = MaterialTheme.typography.bodySmall,
                color = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
