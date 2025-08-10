package com.rejnek.oog.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejnek.oog.ui.screens.GameFinishScreen
import com.rejnek.oog.ui.screens.SecondaryTabScreen
import com.rejnek.oog.ui.screens.GameMenuScreen
import com.rejnek.oog.ui.screens.GameTaskScreen
import com.rejnek.oog.ui.screens.HomeScreen
import com.rejnek.oog.ui.screens.LibraryScreen

@Composable
fun AppRouter() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen.route
    ) {
        // Main screens with bottom navigation
        composable(Routes.HomeScreen.route) {
            HomeScreen(
                onLoadGameClick = {
                    navController.navigate(Routes.GameTaskScreen.route)
                },
                onNavigateToLibrary = {
                    navController.navigate(Routes.LibraryScreen.route)
                }
            )
        }

        composable(Routes.LibraryScreen.route) {
            LibraryScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HomeScreen.route)
                }
            )
        }

        // Game screens without bottom navigation
        composable(Routes.GameTaskScreen.route) {
            BackHandler {
                navController.navigate(Routes.GameMenuScreen.route)
            }

            GameTaskScreen(
                onGoToMenu = {
                    navController.navigate(Routes.GameMenuScreen.route)
                },
                onFinishTask = {
                    navController.navigate(Routes.GameFinishScreen.route)
                }
            )
        }

        composable(Routes.GameFinishScreen.route) {
            BackHandler {  }

            GameFinishScreen(
                onBackToHomeClick = {
                    navController.navigate(Routes.HomeScreen.route)
                }
            )
        }

        composable(Routes.GameMenuScreen.route) {
            GameMenuScreen(
                openTask = {
                    navController.navigate(Routes.GameTaskScreen.route)
                },
                onNavigateToMap = {
                    navController.navigate(Routes.GameMapScreen.route)
                }
            )
        }

        composable(Routes.GameMapScreen.route) {
            SecondaryTabScreen(
                onNavigateToMenu = {
                    navController.navigate(Routes.GameMenuScreen.route)
                },
                onFinishTask = {
                    navController.navigate(Routes.GameFinishScreen.route)
                }
            )
        }
    }
}