package com.rejnek.oog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.rejnek.oog.R
import com.rejnek.oog.ui.navigation.Routes

@Composable
fun BottomNavigationBar(
    currentRoute: Routes,
    onNavigate: (Routes) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            labelRes = R.string.nav_home,
            icon = Icons.Default.Home,
            route = Routes.HomeScreen
        ),
        BottomNavItem(
            labelRes = R.string.nav_library,
            icon = Icons.Default.Menu,
            route = Routes.LibraryScreen
        )
    )

    NavigationBar {
        items.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

data class BottomNavItem(
    val labelRes: Int,
    val icon: ImageVector,
    val route: Routes
)
