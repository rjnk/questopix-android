package com.rejnek.oog.ui.components.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState

@Composable
fun LibraryScreenContent(
    games: List<GamePackage>,
    selectedGameIds: Set<String>,
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
            EmptyLibraryState()
        } else {
            val unfinishedGames = games.filter { it.state != GameState.COMPLETED }
            val completedGames = games.filter { it.state == GameState.COMPLETED }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(unfinishedGames) { game ->
                    GameCard(
                        game = game,
                        isSelected = game.getId() in selectedGameIds,
                        onGameSelected = { onGameSelected(game.getId()) },
                        onGameLongPress = { onGameLongPress(game.getId()) }
                    )
                }

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
                            onGameSelected = { onGameSelected(game.getId()) },
                            onGameLongPress = { onGameLongPress(game.getId()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState() {
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
            text = "+ button to add games",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

