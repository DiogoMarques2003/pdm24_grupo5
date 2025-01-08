package com.kotlin.socialstore.ui.screens.Products

import AddItemDialog
import EditItemDialog
import RowList
import TopBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import com.kotlin.socialstore.viewModels.Products.StockViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
    var productsSelected by remember { mutableStateOf(emptyList<Stock>()) }
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

    if (isLoading.value) {
        LoadIndicator(modifier)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(modifier = modifier.fillMaxSize()) {
            AnimatedVisibility(visible = productsSelected.isEmpty()) {
                TopBar(navController, stringResource(R.string.manageStock_Title), true)
            }

            AnimatedVisibility(visible = productsSelected.isNotEmpty()) {
                Column {
                    SelectionTopBar(
                        selectedCount = productsSelected.size,
                        totalCount = allProducts.size,
                        onSelectAllClick = { isChecked ->
                            productsSelected = if (isChecked) {
                                allProducts.toList()
                            } else {
                                emptyList()
                            }
                        },
                        onCloseSelection = {
                            productsSelected = emptyList()
                        },
                        onDeleteClick = {
                            for (stock in productsSelected) {
                                viewModel.onDelete(stock)
                            }

                            productsSelected = emptyList()
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = productsSelected.size == allProducts.size,
                            onCheckedChange = { isChecked ->
                                productsSelected = if (isChecked) {
                                    allProducts.toList() // Select all
                                } else {
                                    emptyList() // Deselect all
                                }
                                //onSelectAllClick(isChecked)
                            }
                        )
                        Text(
                            text = "Select All",
                            modifier = Modifier.padding(start = 0.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

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
                    selectionBehavior = SelectionBehavior(
                        enabled = true,
                        onItemsSelected = { items ->
                            productsSelected = items
                        },
                        selectedItems = productsSelected
                    )
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

    if (showEditItemDialog == true) {
        EditItemDialog(item, viewModel, { showEditItemDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    totalCount: Int,
    onDeleteClick: () -> Unit,
    onSelectAllClick: (Boolean) -> Unit,
    onCloseSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onCloseSelection) {
                Icon(Icons.Default.Close, contentDescription = "Close selection")
            }
        },
        title = {
            Text("$selectedCount selected")
        },
        actions = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(end = 8.dp)
//            ) {
//                Checkbox(
//                    checked = selectedCount == totalCount && totalCount > 0,
//                    onCheckedChange = { isChecked ->
//                        onSelectAllClick(isChecked)
//                    }
//                )
//                Text(
//                    text = "Select All",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }

            IconButton(onClick = onDeleteClick, enabled = selectedCount > 0) {
                Icon(Icons.Default.Delete, contentDescription = "Delete selected items")
            }

//            // Select All / Deselect All button
//            TextButton(onClick = onSelectAllClick) {
//                Text(
//                    text = if (selectedCount == totalCount) "Deselect All" else "Select All",
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
        },
        modifier = Modifier
    )
}

data class SelectionBehavior<T>(
    val enabled: Boolean = false,
    val onItemsSelected: (List<T>) -> Unit = {},
    val selectedItems: List<T> = emptyList()
)


