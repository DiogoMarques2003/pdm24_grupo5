package com.kotlin.socialstore.ui.screens

import UiConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.Donations.ListDonationsViewModel

@Composable
fun ListDonationsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    listDonationsViewModel: ListDonationsViewModel
) {
    //Variables
    val allDonations by listDonationsViewModel.allDonnations.collectAsState(emptyList())
    val context = LocalContext.current

    //Ui variables
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var displayDonations by remember { mutableStateOf<List<Donations>>(emptyList()) }
    var searchId by remember { mutableStateOf("") }

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            listDonationsViewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        //Get fresh data
        listDonationsViewModel.getData(context)
    }

    // Filter data with donations status when the status or donations data change
    LaunchedEffect(selectedStatus, allDonations, searchId) {
        displayDonations =
            if (selectedStatus == null) allDonations else allDonations.filter { it.state == selectedStatus }

        displayDonations =
            if (searchId.isEmpty()) displayDonations else displayDonations.filter { it.donationId == searchId}
    }

    //Add background image
    BackgroundImageElement()

    Column(modifier = modifier.fillMaxSize()) {
        //Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PopBackButton(navController)
            Spacer(Modifier.size(UiConstants.itemSpacing))
            Text(stringResource(R.string.donations_page_title), fontSize = UiConstants.titleTextSize)
        }

        Spacer(Modifier.size(UiConstants.itemSpacing))
        OutlinedTextfieldElement(
            modifier = Modifier.fillMaxWidth(),
            value = searchId,
            onValueChange = { searchId = it },
            labelText = stringResource(R.string.search_by_id),
            leadingIcon = Icons.Filled.Search
        )

        Spacer(Modifier.size(UiConstants.itemSpacing))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(DataConstants.mapDonationStatus.toList()) { item ->
                AssistChip(
                    onClick = {
                        selectedStatus = if (selectedStatus == item.first) null else item.first
                    },
                    label = { Text(stringResource(item.second)) },
                )
                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
            }
        }

        Spacer(Modifier.size(UiConstants.itemSpacing))
        LazyColumn {
            items(displayDonations) { item ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = UiConstants.itemSpacing),
                    onClick = { navController.navigate("donation_screen/${item.id}") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(text = "${stringResource(R.string.donation)} #${item.donationId}", style = MaterialTheme.typography.headlineSmall)
                            Text(text = "${stringResource(R.string.date)}: ${item.creationDate}", style = MaterialTheme.typography.bodySmall)
                        }

                        if (DataConstants.mapDonationStatus[item.state] != null) {
                            DonationStatus(
                                stringResource(DataConstants.mapDonationStatus[item.state]!!),
                                DataConstants.mapDonationStatusBackgroundColor[item.state]!!,
                                DataConstants.mapDonationStatusTextColor[item.state]!!,
                                DataConstants.mapDonationStatusIcon[item.state]!!,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonationStatus(status: String, backgroundColor: Color, textColor: Color, icon: ImageVector) {
    Row(
        modifier = Modifier.clip(MaterialTheme.shapes.medium)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(5.dp))

        Icon(
            imageVector = icon,
            contentDescription = status,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = status, color = textColor)

        Spacer(modifier = Modifier.size(5.dp))
    }
}