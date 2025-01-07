package com.kotlin.socialstore.ui.screens.Products

import AddItemDialog
import EditItemDialog
import RowList
import TopBar
import com.kotlin.socialstore.viewModels.Products.StockViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.ProductsGrid
import kotlin.math.exp

@Composable
fun ManageStockPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: StockViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    //var productsSelected by remember { mutableStateOf(emptyList<Stock>())}
    val allProducts by viewModel.allStock.collectAsState(emptyList())
    val allCategories by viewModel.allCategories.collectAsState(emptyList())
    val context = LocalContext.current

    val isLoading = viewModel.isLoading.collectAsState(false)

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getData(context)
    }

    if(isLoading.value) {
        LoadIndicator(modifier)
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
                    itemEndContet = { item ->
                        ItemEndContent(item, viewModel)
                    },
                    pictureProvider = { item ->
                        item.picture ?: R.drawable.product_image_not_found
                    },
                    onItemClick = { },
                    setSelectMultipleBehaviour = true

                )
            }


            ButtonElement(
                onClick = { showAddDialog = true },
                text = stringResource(R.string.manageStock_AddItem),
                modifier = Modifier
                    .fillMaxWidth()
            )

//            ButtonElement(
//                onClick = {
//                    for (stock in productsSelected) {
//                        viewModel.onDelete(stock)
//                    }
//                },
//                enabled = productsSelected.isNotEmpty(),
//                text = "Delete items selected",
//                modifier = Modifier
//                    .fillMaxWidth()
//            )

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
        text = "${stringResource(R.string.product_size)} ${item.size?.takeIf { it.isNotEmpty() } ?: "N/A"}" +
                " · ${stringResource(R.string.condition)}: " +
                stringResource(
                    DataConstants.mapProductCondition[item.state] ?: R.string.product_state_default
                ),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ItemEndContent(item: Stock, viewModel: StockViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showEditItemDialog by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    showEditItemDialog = true
                }
            ) {
                Text(text = "Edit")
            }

            DropdownMenuItem(
                onClick = {
                    expanded = false
                    viewModel.onDelete(item)
                }
            ) {
                Text(text = "Delete")
            }
        }
    }

    if(showEditItemDialog == true) {
        EditItemDialog(item, viewModel, {showEditItemDialog = false})
    }
}


