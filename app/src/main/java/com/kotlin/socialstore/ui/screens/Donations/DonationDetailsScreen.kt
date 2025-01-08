package com.kotlin.socialstore.ui.screens.Donations

import TopBar
import UiConstants
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.dialogs.ConfirmPopup
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.viewModels.Donations.DonationDetailsViewModel
import formatWeekDay

@Composable
fun DonationDetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    donationDetailsViewModel: DonationDetailsViewModel
) {
    val donationData by donationDetailsViewModel.donationData.collectAsState(null)
    val donationItems by donationDetailsViewModel.donationItems.collectAsState(emptyList())
    val allDonationSchedule by donationDetailsViewModel.allDonationSchedule.collectAsState(emptyList())
    val finishedLoadingData by donationDetailsViewModel.finishedLoadingData.collectAsState(false)
    val isLoading by donationDetailsViewModel.isLoading.collectAsState(false)
    val context = LocalContext.current

    var shopConfirmPopup = remember { mutableStateOf(false) }

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

    if ((donationData == null || donationItems.isEmpty() || allDonationSchedule.isEmpty()) && finishedLoadingData) {
        LaunchedEffect(Unit) {
            Toast.makeText(
                context, context.getString(R.string.donation_not_found), Toast.LENGTH_SHORT
            ).show()
            navController.popBackStack()
        }
    }

    if (isLoading) {
        LoadIndicator(modifier)
    }

    if (shopConfirmPopup.value) {
        ConfirmPopup(
            title = stringResource(R.string.donation_delivered),
            text = stringResource(R.string.insert_donation_item),
            confirmButton = {
                shopConfirmPopup.value = false
                navController.navigate("donation_insert_product/${donationDetailsViewModel.donationId}")
            },
            dismissButton = {
                shopConfirmPopup.value = false
                donationDetailsViewModel.updateDonationStatus(
                    DataConstants.donationDoneStatus,
                    context
                )
            },
            showPopup = shopConfirmPopup
        )
    }

    //Add background image
    BackgroundImageElement()

    Column(modifier = modifier.fillMaxSize()) {
        //Top
        TopBar(navController, stringResource(R.string.donation_details_page_title))

        if (donationData == null || donationItems.isEmpty() || allDonationSchedule.isEmpty()) {
            LoadIndicator(modifier)
        } else {
            val donationSchedule =
                allDonationSchedule.find { it.id == donationData!!.donationScheduleID }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.information))
                Text(
                    "${stringResource(R.string.identifier)} #${donationData!!.donationId}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${stringResource(R.string.location)} ${donationSchedule?.local}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${stringResource(R.string.schedule)} ${formatWeekDay(donationSchedule?.weekDay ?: 0)}, ${donationSchedule?.startTime} - ${donationSchedule?.endTime}",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))

                Text(stringResource(R.string.contact_details))
                Text(
                    "${stringResource(R.string.name)}: ${donationData!!.donaterName}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${stringResource(R.string.email_textfield)}: ${donationData!!.email}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${stringResource(R.string.phoneNumber_textfield)}: ${donationData!!.phoneCountryCode} ${donationData!!.phoneNumber}",
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(top = 10.dp)
            ) {
                items(donationItems) { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = UiConstants.itemSpacing),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.picture != null) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SubcomposeAsyncImage(
                                        model = item.picture,
                                        contentDescription = null,
                                        loading = { CircularProgressIndicator() },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(40.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(UiConstants.itemSpacing))
                            }

                            // Item details
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.description!!,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Row {
                                    if (!item.size.isNullOrEmpty()) {
                                        Text(
                                            text = "${stringResource(R.string.product_size)} ${item.size} - ",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = "${stringResource(R.string.qty)} ${item.quantity}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (donationData!!.state == DataConstants.donationInitialStatus) {
                    ButtonElement(
                        text = stringResource(R.string.aprove),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            donationDetailsViewModel.updateDonationStatus(
                                DataConstants.donationAppprovedStatuus,
                                context
                            )
                        }
                    )

                    Spacer(Modifier.size(UiConstants.itemSpacing))

                    ButtonElement(
                        text = stringResource(R.string.decline),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            donationDetailsViewModel.updateDonationStatus(
                                DataConstants.donationDeclinedStatus,
                                context
                            )
                        }
                    )
                }

                if (donationData!!.state == DataConstants.donationAppprovedStatuus) {
                    ButtonElement(
                        text = stringResource(R.string.donation_delivered),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { shopConfirmPopup.value = true }
                    )
                }
            }
        }
    }
}