package com.rejnek.oog.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes(val route: String) {
    @Serializable
    data object HomeScreen : Routes("HomeScreen")

    @Serializable
    data object GameStartScreen : Routes("GameStartScreen")

    @Serializable
    data object GameNavigationTextScreen : Routes("GameNavigationTextScreen")

    @Serializable
    data object GameTaskScreen : Routes("GameTaskScreen")

    @Serializable
    data object GameFinishScreen : Routes("GameFinishScreen")
}