package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.components.home.OOGLogo
import com.rejnek.oog.ui.navigation.Routes
import com.rejnek.oog.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLoadGameClick: () -> Unit,
    onNavigateToLibrary: () -> Unit = {},
    onLoadGameFromFileViaLibrary: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.HomeScreen,
                onNavigate = { route ->
                    when (route) {
                        Routes.LibraryScreen -> onNavigateToLibrary()
                        Routes.HomeScreen -> { /* Already on Home, no action needed */ }
                        else -> { /* Handle other routes if needed */ }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        HomeScreenContent(
            onLoadSavedClicked = {
                viewModel.onLoadSavedClicked()
                onLoadGameClick()
            },
            showSavedGame = viewModel.hasSavedGame.collectAsState().value,
            onNavigateToLibrary = onNavigateToLibrary,
            onImportGameViaLibrary = onLoadGameFromFileViaLibrary,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeScreenContent(
    onLoadSavedClicked: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onImportGameViaLibrary: () -> Unit,
    showSavedGame: Boolean,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        OOGLogo()
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if( showSavedGame ){
                Button(
                    onClick = onLoadSavedClicked,
                    modifier = Modifier.height(56.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.continue_game))
                    }
                )
            }
            else{
                Button(
                    onClick = {
                        onNavigateToLibrary()
                    },
                    modifier = Modifier.height(56.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.play_new_game))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onImportGameViaLibrary()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 6.dp)
                        )
                        Text(stringResource(R.string.load_game))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
        }
    }
}
