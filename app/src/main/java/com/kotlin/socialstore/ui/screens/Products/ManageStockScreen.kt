package com.kotlin.socialstore.ui.screens.Products

import AddItemDialog
import RowList
import TopBar
import com.kotlin.socialstore.viewModels.Products.StockViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.ProductsGrid

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
        viewModel.getData(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(modifier = modifier.fillMaxSize()) {
            TopBar(navController, stringResource(R.string.manageStock_Title), true)

            Box(modifier = Modifier.weight(1f)) {
                RowList(
                    items = allProducts,
                    itemContent = { item ->
                        ItemContent(item)
                    },
                    pictureProvider = { item ->
                        item.picture ?: R.drawable.product_image_not_found
                    },
                    onItemClick = { }
                )
            }


            ButtonElement(
                onClick = { showAddDialog = true },
                text = stringResource(R.string.manageStock_AddItem),
                modifier = Modifier
                    .fillMaxWidth()
            )

        }
    }



    if (showAddDialog) {
        AddItemDialog(
            viewModel = viewModel,
            modifier = Modifier.padding(horizontal = 16.dp),
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun ItemContent(item: Stock) {
    Text(
        text = item.description,
        style = MaterialTheme.typography.bodyLarge
    )

    Text(
        text = "${stringResource(R.string.product_size)} ${item.size?.takeIf { it.isNotEmpty() } ?: "N/A"}",
        style = MaterialTheme.typography.bodyMedium
    )

    // add category then
}

