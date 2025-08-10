package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.components.BottomNavigationBar
import com.rejnek.oog.ui.components.OOGLogo
import com.rejnek.oog.ui.navigation.Routes
import com.rejnek.oog.ui.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onLoadGameClick: () -> Unit,
    onNavigateToLibrary: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {

    Scaffold(
        topBar = {  },
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
            onLoadAssetGameClick = {
                viewModel.onLoadAssetGameClicked()
                onLoadGameClick()
            },
            onLoadSavedClicked = {
                viewModel.onLoadSavedClicked()
                onLoadGameClick()
            },
            showSavedGame = viewModel.hasSavedGame.collectAsState().value,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeScreenContent(
    onLoadAssetGameClick: () -> Unit,
    onLoadSavedClicked: () -> Unit,
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
                        Text("Continue Game")
                    }
                )
            }
            else{
                Button(
                    onClick = { },
                    modifier = Modifier.height(56.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Play a New Game")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onLoadAssetGameClick,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 6.dp)
                        )
                        Text("Load game")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
        }
    }
}
