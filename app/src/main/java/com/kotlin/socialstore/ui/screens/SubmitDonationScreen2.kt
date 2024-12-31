package com.kotlin.socialstore.ui.screens

import AddDonationItemDialog
import AddItemDialog
import DonationViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.TitleTextElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitDonationPage2(
    navController: NavController,
    viewModel: DonationViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showLocationDropdown by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    val donationItems = viewModel.donationItems.collectAsState(initial = emptyList())
    val stores by viewModel.allStores.collectAsState(initial = emptyList())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TitleTextElement(
            text = "New Donation",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextfieldElement(
            value = fullName,
            onValueChange = { fullName = it },
            labelText = "Full Name",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        OutlinedTextfieldElement(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            labelText = "Phone Number",
            keyboardType = KeyboardType.Phone,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        OutlinedTextfieldElement(
            value = email,
            onValueChange = { email = it },
            labelText = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        ExposedDropdownMenuBox(
            expanded = showLocationDropdown,
            onExpandedChange = { showLocationDropdown = it }
        ) {
            OutlinedTextfieldElement(
                value = location,
                onValueChange = {},
                readOnly = true,
                labelText = "Location",
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLocationDropdown) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            ExposedDropdownMenu(
                expanded = showLocationDropdown,
                onDismissRequest = { showLocationDropdown = false }
            ) {
                stores.forEach { loc ->
                    DropdownMenuItem(
                        text = { Text(loc.name) },
                        onClick = {
                            location = loc.id
                            showLocationDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        ButtonElement(
            onClick = { showAddItemDialog = true },
            modifier = Modifier.fillMaxWidth(),
            text = "Add new item",
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            items(donationItems.value) { item ->
                DonationItemRow(item)
            }
        }

        ButtonElement(
            onClick = { /* Handle submission */ },
            modifier = Modifier.fillMaxWidth(),
            text = "Submit Donation"
        )
    }

    if (showAddItemDialog) {
        AddDonationItemDialog(
            viewModel = viewModel,
            modifier = Modifier.padding(horizontal = 16.dp),
            onDismiss = { showAddItemDialog = false }
        )
    }
}

@Composable
fun DonationItemRow(item: DonationsItems) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.description?.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = item.description!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Size: ${item.size} · Qty: ${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { /* Handle click */ }) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}