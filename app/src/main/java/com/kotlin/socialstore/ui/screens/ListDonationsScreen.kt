package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.ListDonationsViewModel

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
    LaunchedEffect(selectedStatus, allDonations) {
        displayDonations =
            if (selectedStatus == null) allDonations else allDonations.filter { it.state == selectedStatus }
    }

    //Add background image
    BackgroundImageElement()

    Column(modifier.fillMaxSize()) {
        //Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PopBackButton(navController)
            Spacer(Modifier.size(UiConstants.itemSpacing))
            Text(stringResource(R.string.products_page_title), fontSize = UiConstants.titleTextSize)
        }
    }

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

}