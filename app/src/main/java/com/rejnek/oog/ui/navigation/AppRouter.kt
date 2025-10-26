package com.rejnek.oog.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejnek.oog.ui.screens.GameInfoScreen
import com.rejnek.oog.ui.screens.GameTaskScreen
import com.rejnek.oog.ui.screens.HomeScreen
import com.rejnek.oog.ui.screens.LibraryScreen
import com.rejnek.oog.ui.screens.OnboardingScreen
import com.rejnek.oog.ui.screens.SettingsScreen
import com.rejnek.oog.ui.viewmodel.SharedEventsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.activity.compose.LocalActivity

@Composable
fun AppRouter() {
    val navController: NavHostController = rememberNavController()
    val sharedEvents: SharedEventsViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.OnboardingScreen.route
    ) {
        composable(Routes.OnboardingScreen.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.OnboardingScreen.route) { inclusive = true }
                    }
                }
            )
        }

        // Main screens with bottom navigation
        composable(Routes.HomeScreen.route) {
            // Ensure back from Home closes the app
            val activity = LocalActivity.current
            BackHandler {
                activity?.finish()
            }

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
            // Ensure back from Library always returns to Home
            BackHandler {
                navController.navigate(Routes.HomeScreen.route) {
                    popUpTo(Routes.HomeScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

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

        composable(Routes.GameInfoScreen("{gameId}").route) { backStackEntry ->
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
                onGoToMenu = {
                    navController.navigate(Routes.HomeScreen.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Game screens without bottom navigation
        composable(Routes.GameTaskScreen.route) {
            GameTaskScreen(
                onFinishTask = {
                    navController.navigate(Routes.LibraryScreen.route)
                },
                onOpenSettings = {
                    navController.navigate(Routes.SettingsScreen.route)
                }
            )
        }
    }
}
