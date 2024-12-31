package com.kotlin.socialstore.ui.screens

import AddItemDialog
import DonationItem
import StockViewModel
import android.net.Uri
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.ProductsGrid
import com.kotlin.socialstore.ui.elements.TitleTextElement

@Composable
fun ManageStockPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val allProducts by viewModel.allStock.collectAsState(emptyList())
    val allCategories by viewModel.allCategories.collectAsState(emptyList())
    val context = LocalContext.current

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        //Get fresh data
        viewModel.getData(context)
    }


    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        Column(modifier = modifier.fillMaxSize()) {
            TitleTextElement(
                text = stringResource(R.string.manageStock_Title),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProductsGrid(allProducts, allCategories)

            if (showAddDialog) {
                AddItemDialog(
                    viewModel = viewModel,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onDismiss = { showAddDialog = false }
                )
            }
        }
    }
    Box(modifier = modifier
        .fillMaxSize()
    ) {
        ButtonElement(
            onClick = { showAddDialog = true },
            text = stringResource(R.string.manageStock_AddItem),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

