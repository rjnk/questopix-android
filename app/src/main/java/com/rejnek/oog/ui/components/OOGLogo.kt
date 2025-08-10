package com.rejnek.oog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.rejnek.oog.R

@Composable
fun OOGLogo(
    modifier: Modifier = Modifier,
){
    Image (
        painter = painterResource(id = R.drawable.ooglogo),
        contentDescription = "Game icon",
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    )
}