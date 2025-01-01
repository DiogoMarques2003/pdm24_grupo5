package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.DonationDetailsViewModel

@Composable
fun DonationDetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    donationDetailsViewModel: DonationDetailsViewModel
) {
    val donationData by donationDetailsViewModel.donationData.collectAsState(null)
    val donationItems by donationDetailsViewModel.donationItems.collectAsState(emptyList())
    val allDonationSchedule by donationDetailsViewModel.allDonationSchedule.collectAsState(emptyList())
    val context = LocalContext.current

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            donationDetailsViewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        //Get fresh data
        donationDetailsViewModel.getData(context)
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
            Text(
                stringResource(R.string.donation_details_page_title),
                fontSize = UiConstants.titleTextSize
            )
        }

        Spacer(modifier = Modifier.size(UiConstants.itemSpacing))

        if (donationData == null || donationItems.isEmpty()) {
            LoadIndicator(modifier)
        } else {
            val donationSchedule = allDonationSchedule.find { it.id == donationData!!.donationScheduleID }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Identifier: #${donationData!!.donationId}")
                Text("Location: ${donationSchedule!!.local}")
                Text("Schedule: ${donationSchedule.weekDay}, ${donationSchedule.startTime} - ${donationSchedule.endTime}")

                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))

                Text("Contact details:")
                Text("Name: ${donationData!!.donaterName}")
                Text("E-mail: ${donationData!!.email}")
                Text("Phone: ${donationData!!.phoneCountryCode} ${donationData!!.phoneNumber}")
            }

            Text("Items:")
            LazyColumn(Modifier.weight(1f)) {
                items(donationItems) {item ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("${item.description} (Size: ${item.size ?: "N/A"}, Qty: ${item.quantity})")
                    }
                }
            }
        }
    }
}