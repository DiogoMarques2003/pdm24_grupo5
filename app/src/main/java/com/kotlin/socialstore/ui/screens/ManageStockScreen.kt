package com.kotlin.socialstore.ui.screens

import AddDonationItemDialog
import AddItemDialog
import DonationItem
import StockViewModel
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.TitleTextElement

@Composable
fun ManageStockPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        TitleTextElement(
            text = stringResource(R.string.manageStock_Title),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (showAddDialog) {
            //AddDonationItemDialog(
            //    navController = navController,
            //    modifier = modifier,
            //    onDismiss = { showAddDialog = false },
            //    onPhotoSelected = { uri -> currentPhotoUri = uri }
            //)
            AddItemDialog(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = 16.dp),
                onDismiss = { showAddDialog = false }
            )
        }

        ButtonElement(
            onClick = { showAddDialog = true },
            text = stringResource(R.string.manageStock_AddItem),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

