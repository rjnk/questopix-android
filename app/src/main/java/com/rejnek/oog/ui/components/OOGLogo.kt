package com.rejnek.oog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun OOGLogo(
    modifier: Modifier = Modifier,
){
    Image (
        imageVector = Icons.Outlined.Explore,
        contentDescription = "Game icon",
        modifier = modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1f),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}