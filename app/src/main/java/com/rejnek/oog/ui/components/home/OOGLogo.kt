package com.rejnek.oog.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R

/**
 * App logo displayed on the home screen.
 *
 * @param modifier Modifier for customizing the logo layout
 */
@Composable
fun OOGLogo(
    modifier: Modifier = Modifier,
){
    Image (
        painter = painterResource(id = R.drawable.questopix_logo),
        contentDescription = stringResource(R.string.cd_game_icon),
        modifier = modifier
            .fillMaxWidth(0.8f)
            .padding(top = 25.dp)
            .aspectRatio(1f),
    )
}