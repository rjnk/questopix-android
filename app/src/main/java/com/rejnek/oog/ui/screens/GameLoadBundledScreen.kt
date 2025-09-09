package com.rejnek.oog.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejnek.oog.ui.viewmodels.LoadBundledViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameLoadBundledScreen(
    onGoToHome: () -> Unit,
    viewModel: LoadBundledViewModel = koinViewModel()
) {
    val isLoading = viewModel.isLoading.collectAsState().value

    if(isLoading){
        Text(text = "Loading", modifier = Modifier.padding(64.dp))

    }
    else{
        onGoToHome()
    }
}