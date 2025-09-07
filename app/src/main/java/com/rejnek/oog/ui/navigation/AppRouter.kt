package com.rejnek.oog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejnek.oog.ui.screens.GameInfoScreen
import com.rejnek.oog.ui.screens.GameTaskScreen
import com.rejnek.oog.ui.screens.HomeScreen
import com.rejnek.oog.ui.screens.LibraryScreen
import com.rejnek.oog.ui.screens.SettingsScreen
import com.rejnek.oog.ui.viewmodels.SharedEventsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRouter() {
    val navController: NavHostController = rememberNavController()
    val sharedEvents: SharedEventsViewModel = koinViewModel()

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
                    navController.navigate(Routes.LibraryScreen.route) {
                        popUpTo(Routes.HomeScreen.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SettingsScreen.route)
                },
                onLoadGameFromFileViaLibrary = {
                    // set flag then navigate; LibraryScreen will consume and launch picker
                    sharedEvents.triggerImportGame()
                    navController.navigate(Routes.LibraryScreen.route) {
                        popUpTo(Routes.HomeScreen.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Routes.LibraryScreen.route) {
            LibraryScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.HomeScreen.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SettingsScreen.route)
                },
                onNavigateToGameInfo = { gameId ->
                    navController.navigate("GameInfoScreen/$gameId")
                },
                sharedEvents = sharedEvents
            )
        }

        composable("GameInfoScreen/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            GameInfoScreen(
                gameId = gameId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onGameStarted = {
                    navController.navigate(Routes.GameTaskScreen.route)
                }
            )
        }

        composable(Routes.SettingsScreen.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Game screens without bottom navigation
        composable(Routes.GameTaskScreen.route) {
            GameTaskScreen(
                onFinishTask = {
                    navController.navigate(Routes.HomeScreen.route)
                },
                onOpenSettings = {
                    navController.navigate(Routes.SettingsScreen.route)
                }
            )
        }
    }
}
