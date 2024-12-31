package com.kotlin.socialstore.ui.screens

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Text("Main Screen")

    Button(onClick = { navController.navigate("list_donations_screen") }) { Text("Login") }
}