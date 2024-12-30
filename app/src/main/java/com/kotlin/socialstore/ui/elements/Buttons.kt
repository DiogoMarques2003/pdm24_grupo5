package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ButtonElement(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        modifier = modifier.size(50.dp),
        onClick = onClick,
        shape = RoundedCornerShape(35),
        enabled = enabled
    ) {
        Text(text = text, fontSize = 15.sp)
    }
}

@Composable
fun PopBackButton(navController : NavController){
    FloatingActionButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier.width(30.dp),
        containerColor = MaterialTheme.colorScheme.background,
        elevation = FloatingActionButtonDefaults.loweredElevation(0.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = "Go back",
            tint = Color.Black
        )
    }
}