package com.kotlin.socialstore.ui.screens.Donations

import AddDonationItemDialog
import TopBar
import com.kotlin.socialstore.viewModels.Donations.DonationViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.togitech.ccp.component.TogiCountryCodePicker
import formatWeekDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitDonationPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DonationViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCountryCode by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var locationID by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var showLocationDropdown by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    val donationItems = viewModel.donationItems.collectAsState(initial = emptyList())
    val locations by viewModel.allLocations.collectAsState(initial = emptyList())

    BackgroundImageElement()

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(navController, "New Donation", true)

        OutlinedTextfieldElement(
            value = fullName,
            onValueChange = { fullName = it },
            labelText = "Full Name",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        TogiCountryCodePicker(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { (code, phone), valid ->
                phoneNumber = phone
                phoneCountryCode = code
                isPhoneNumberValid = valid
            },
            label = { Text(stringResource(R.string.phoneNumber_textfield)) },
            showError = phoneNumber != "",
            clearIcon = null,
            initialCountryIsoCode = "PT",
            shape = UiConstants.outlinedTextFieldElementShape,
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
                value = locationName,
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
                locations.forEach { loc ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(loc.local)
                                Text(
                                    "${formatWeekDay(loc.weekDay)}, ${loc.startTime} - ${loc.endTime}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            locationID = loc.id
                            locationName = loc.local
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
            onClick = { viewModel.submitDonation(
                fullName = fullName,
                phoneNumber = phoneNumber,
                phoneCountryCode = phoneCountryCode,
                email = email,
                locationID = locationID
            ) },
            modifier = Modifier.fillMaxWidth(),
            text = "Submit Donation",
            enabled = donationItems.value.isNotEmpty() && isPhoneNumberValid && email.isNotBlank() && fullName.isNotBlank() && locationID.isNotBlank()
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