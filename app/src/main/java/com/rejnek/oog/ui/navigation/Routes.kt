package com.rejnek.oog.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes(val route: String) {
    @Serializable
    data object HomeScreen : Routes("HomeScreen")

    @Serializable
    data object GameTaskScreen : Routes("GameTaskScreen")

    @Serializable
    data object GameFinishScreen : Routes("GameFinishScreen")

    @Serializable
    data object GameMenuScreen : Routes("GameMenuScreen")

    @Serializable
    data object GameMapScreen : Routes("GameMapScreen")

    @Serializable
    data object LibraryScreen : Routes("LibraryScreen")

    @Serializable
    data object SettingsScreen : Routes("SettingsScreen")
}
