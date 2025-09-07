package com.rejnek.oog.ui.share

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.staticCompositionLocalOf

// Provides access to the GameTaskScreen's ScrollState so components like ShareButton
// can capture the entire scrollable content.
val LocalGameTaskScrollState = staticCompositionLocalOf<ScrollState> { error("GameTask ScrollState not provided") }

