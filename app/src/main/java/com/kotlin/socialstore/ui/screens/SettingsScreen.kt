package com.kotlin.socialstore.ui.screens

import TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement


@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navController: NavController) {

    BackgroundImageElement()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopBar(navController, stringResource(R.string.contactus))

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
        ) {
            Text(text = "Manage Profile")
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
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
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.size(50.dp),
            color = MaterialTheme.colorScheme.primary,
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

        Text(text = location, style = MaterialTheme.typography.bodyMedium)
    }
}

