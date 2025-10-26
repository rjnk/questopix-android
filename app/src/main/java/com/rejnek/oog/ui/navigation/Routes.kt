package com.rejnek.oog.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes(val route: String) {
    @Serializable
    data object OnboardingScreen : Routes("OnboardingScreen")

    @Serializable
    data object HomeScreen : Routes("HomeScreen")

    @Serializable
    data object GameTaskScreen : Routes("GameTaskScreen")

    @Serializable
    data object LibraryScreen : Routes("LibraryScreen")

    @Serializable
    data class GameInfoScreen(val gameId: String) : Routes("GameInfoScreen/{gameId}")

    @Serializable
    data object SettingsScreen : Routes("SettingsScreen")
}
