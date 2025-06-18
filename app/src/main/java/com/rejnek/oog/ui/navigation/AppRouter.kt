package com.rejnek.oog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejnek.oog.ui.screens.GameFinishScreen
import com.rejnek.oog.ui.screens.GameTaskScreen
import com.rejnek.oog.ui.screens.HomeScreen

@Composable
fun AppRouter() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen.route
    ) {
        composable(Routes.HomeScreen.route) {
            HomeScreen(
                onLoadGameClick = {
                    navController.navigate(Routes.GameTaskScreen.route)
                }
            )
        }

        composable(Routes.GameTaskScreen.route) {
            GameTaskScreen(
                onFinishTask = {
                    navController.navigate(Routes.GameFinishScreen.route) {
                        popUpTo(Routes.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.GameFinishScreen.route) {
            GameFinishScreen(
                onBackToHomeClick = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}