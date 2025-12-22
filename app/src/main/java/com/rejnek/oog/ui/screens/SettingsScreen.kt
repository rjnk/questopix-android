/*
 * Created with Github Copilot
 */
package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
import com.rejnek.oog.ui.components.settings.ChangeLanguageButton
import com.rejnek.oog.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Settings screen with app info and in-game controls.
 *
 * When accessed during gameplay, displays game info with pause/quit options.
 * Always shows app information, language settings, and external links.
 *
 * @param onNavigateBack Callback for back navigation
 * @param onGoToMenu Callback to navigate to main menu (after pause/quit)
 * @param viewModel ViewModel managing settings and game state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onGoToMenu: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val openFromGame = viewModel.openFromGame.collectAsState()
    val gameName = viewModel.gameName.collectAsState()
    val gameDescription = viewModel.gameDescription.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                        )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            val context = LocalContext.current
            var showQuitDialog by remember { mutableStateOf(false) }
            // Game section
            if(openFromGame.value){
                // Game name
                Text(gameName.value ?: "", style = MaterialTheme.typography.headlineMedium)
                // Game description
                Text(gameDescription.value ?: "")
                // put the Pause and Quit buttons side by side, making the Pause button take 2/3 of the width and Quit 1/3
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.pauseGame()
                            onGoToMenu()
                        },
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(stringResource(R.string.pause_game))
                    }
                    Button(
                        onClick = { showQuitDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ){
                        Text(stringResource(R.string.quit))
                    }
                }
                // divider
                Spacer(modifier = Modifier.padding(32.dp))
            }
            if(showQuitDialog){
                AlertDialog(
                    onDismissRequest = { showQuitDialog = false },
                    title = { Text(stringResource(R.string.quit_game_title)) },
                    text = { Text(stringResource(R.string.quit_game_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.quitGame()
                            onGoToMenu()
                            showQuitDialog = false
                        }) { Text(stringResource(R.string.quit)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showQuitDialog = false }) { Text(stringResource(R.string.cancel)) }
                    }
                )
            }
            // About section
            Text(stringResource(R.string.about_oog_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.about_oog_description))
            ChangeLanguageButton(context)
            // put the buttons side by side, making them take equal width
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.openUrl(context, "https://github.com/rjnk/questopix-android") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.project_website))
                }
                Button(
                    // make the color slightly dimmer to indicate it's not the main website
                    onClick = { viewModel.openUrl(context, "https://github.com/rjnk/questopix-android") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.privacy_policy))
                }
            }
        }
    }
}