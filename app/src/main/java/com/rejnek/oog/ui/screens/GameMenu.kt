package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.ui.components.GameNavBar
import com.rejnek.oog.ui.viewmodels.GameMenuViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameMenuScreen(
    openTask: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: GameMenuViewModel = koinViewModel()
){
    val visibleElements = viewModel.visibleElements.collectAsState()
    var selectedIndex by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            GameNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    if (index == 1) onNavigateToMap()
                }
            )
        }
    ) {
        GameMenuContent(
            visibleElements = visibleElements,
            onElementClick = {
                viewModel.clickOnElement(it)
                openTask()
            },
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun GameMenuContent(
    visibleElements: State<List<GameElement>>,
    onElementClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.headlineMedium,
        )
        for (element in visibleElements.value) {
            GameMenuElement(
                element = element,
                onClick = { onElementClick(element.id) },
            )
        }
    }
}

@Composable
fun GameMenuElement(
    element: GameElement,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(element.id) },
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Text(
            element.name,
            modifier = Modifier.padding(8.dp),
        )
    }
}