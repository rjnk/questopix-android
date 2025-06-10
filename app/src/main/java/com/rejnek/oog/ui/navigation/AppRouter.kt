package com.rejnek.oog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejnek.oog.ui.screens.GameFinishScreen
import com.rejnek.oog.ui.screens.GameNavigationTextScreen
import com.rejnek.oog.ui.screens.GameStartScreen
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
                    navController.navigate(Routes.GameStartScreen.route)
                }
            )
        }

        composable(Routes.GameStartScreen.route) {
            GameStartScreen(
                onContinueClick = {
                    navController.navigate(Routes.GameNavigationTextScreen.route)
                }
            )
        }

        composable(Routes.GameNavigationTextScreen.route) {
            GameNavigationTextScreen(
                onNextNavigation = {
                    navController.navigate(Routes.GameNavigationTextScreen.route)
                },
                onNextTask = {
                    navController.navigate(Routes.GameTaskScreen.route)
                },
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