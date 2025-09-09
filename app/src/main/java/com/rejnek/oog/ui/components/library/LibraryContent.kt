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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import androidx.compose.ui.res.stringResource
import com.rejnek.oog.R

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
            val inProgressGames = games.filter { it.state == GameState.IN_PROGRESS }
            val newGames = games.filter { it.state == GameState.NOT_STARTED }
            val ARCHIVEDGames = games.filter { it.state == GameState.ARCHIVED }
            val disableOtherStates = inProgressGames.isNotEmpty()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(inProgressGames.isNotEmpty()) {
                    item{ SectionHeading(stringResource(R.string.games_in_progress)) }
                    items(inProgressGames) { game ->
                        GameCard(
                            game = game,
                            isSelected = game.getId() in selectedGameIds,
                            onGameSelected = { onGameSelected(game.getId()) },
                            onGameLongPress = { onGameLongPress(game.getId()) },
                            isEnabled = true
                        )
                    }
                    item{ HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }
                }
                if(newGames.isNotEmpty()){
                    item{ SectionHeading(stringResource(R.string.games_to_play)) }
                    items(newGames) { game ->
                        GameCard(
                            game = game,
                            isSelected = game.getId() in selectedGameIds,
                            onGameSelected = { onGameSelected(game.getId()) },
                            onGameLongPress = { onGameLongPress(game.getId()) },
                            isEnabled = !disableOtherStates
                        )
                    }
                }
                if (ARCHIVEDGames.isNotEmpty()) {
                    item{ SectionHeading(stringResource(R.string.completed_games)) }
                    items(ARCHIVEDGames) { game ->
                        GameCard(
                            game = game,
                            isSelected = game.getId() in selectedGameIds,
                            onGameSelected = { onGameSelected(game.getId()) },
                            onGameLongPress = { onGameLongPress(game.getId()) },
                            isEnabled = !disableOtherStates
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(
    title: String
){
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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
            text = stringResource(R.string.empty_library_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_library_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
