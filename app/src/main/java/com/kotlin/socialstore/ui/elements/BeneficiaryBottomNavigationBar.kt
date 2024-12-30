package com.kotlin.socialstore.ui.elements

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
            selected = currentRoute == "home_screen",
            onClick = { navController.navigate("home_screen") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile_page_screen",
            onClick = { navController.navigate("profile_page_screen") },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Profile") },
            label = { Text("Catalog") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile_page_screen",
            onClick = { navController.navigate("profile_page_screen") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") },
            label = { Text("Settings") }
        )
    }
}
