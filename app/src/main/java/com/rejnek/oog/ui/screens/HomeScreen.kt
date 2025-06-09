package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rejnek.oog.ui.components.OOGLogo

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onLoadGameClick = {})
}

@Composable
fun HomeScreen(
    onLoadGameClick: () -> Unit
) {
    Scaffold(
        topBar = {  },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        HomeScreenContent(
            onLoadGameClick = onLoadGameClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeScreenContent(
    onLoadGameClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        OOGLogo()
        Button(
            onClick = onLoadGameClick,
            content = {
                Text("Load a game")
            },
        )
    }
}
