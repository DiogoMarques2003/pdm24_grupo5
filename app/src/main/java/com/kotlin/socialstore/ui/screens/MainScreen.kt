package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

    Column(modifier.fillMaxSize()) {
        Button(onClick = { navController.navigate("list_donations_screen") }) { Text("Listar doações") }
    }
}