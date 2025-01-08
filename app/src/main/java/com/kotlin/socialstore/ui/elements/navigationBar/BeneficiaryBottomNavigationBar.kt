package com.kotlin.socialstore.ui.elements.navigationBar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings

@Composable
fun BeneficiaryBottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "main_screen",
            onClick = { navController.navigate("main_screen") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "products_screen",
            onClick = { navController.navigate("products_screen") },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Products") },
            label = { Text("Catalog") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings_screen",
            onClick = { navController.navigate("settings_screen") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}
