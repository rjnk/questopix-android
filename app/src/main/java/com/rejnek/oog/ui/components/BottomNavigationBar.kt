package com.rejnek.oog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rejnek.oog.ui.navigation.Routes

@Composable
fun BottomNavigationBar(
    currentRoute: Routes,
    onNavigate: (Routes) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = Routes.HomeScreen
        ),
        BottomNavItem(
            label = "Library",
            icon = Icons.Default.Menu,
            route = Routes.LibraryScreen
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Routes
)
