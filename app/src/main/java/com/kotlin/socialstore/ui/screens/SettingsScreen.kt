package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.PopBackButton


@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                backgroundColor = Color(0xFF13232C),
                contentColor = Color.White
            )
        }

    ) { innerPadding ->
        BackgroundImageElement()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Contact us",
                style = MaterialTheme.typography.headlineSmall
            )

            ContactItem(
                icon = Icons.Default.Home,
                text = "Edifício Pé Alado, Largo Carlos Amarante 181, 4700-308 Braga"
            )
            ContactItem(
                icon = Icons.Default.Phone,
                text = "+351 253 217 234"
            )
            ContactItem(
                icon = Icons.Default.Email,
                text = "freguesia@saolazaro-braga.com.pt"
            )

            // Informações de horário
            ScheduleItem(
                dayOfWeek = "Saturday",
                hours = "14h-16h",
                location = "Braga",
                iconLabel = "SAT"
            )
            ScheduleItem(
                dayOfWeek = "Sunday",
                hours = "9h-11h",
                location = "Braga",
                iconLabel = "SUN"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("profile_page_screen") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                     Color(0xFF13232C),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Manage Profile")
            }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF13232C)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ScheduleItem(dayOfWeek: String, hours: String, location: String, iconLabel: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone/label do dia
        Surface(
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF13232C),
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = iconLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = dayOfWeek, style = MaterialTheme.typography.bodyMedium)
            Text(text = hours, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Localização (Braga)
        Text(text = location, style = MaterialTheme.typography.bodyMedium)
    }
}

