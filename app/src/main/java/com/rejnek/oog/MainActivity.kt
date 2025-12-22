package com.rejnek.oog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rejnek.oog.ui.navigation.AppRouter
import com.rejnek.oog.ui.theme.AppTheme

/**
 * Main activity for Questopix.
 *
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppRouter()
            }
        }
    }
}