package com.rejnek.oog.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rejnek.oog.R

@Composable
fun OOGLogo(
    modifier: Modifier = Modifier,
){
    Image (
        painter = painterResource(id = R.drawable.ooglogo),
        contentDescription = stringResource(R.string.cd_game_icon),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    )
}