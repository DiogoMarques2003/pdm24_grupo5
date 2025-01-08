package com.kotlin.socialstore.ui.screens.Donations

import TopBar
import UiConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.viewModels.Donations.InsertItemsDonationViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertItemsDonationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    insertItemsDonationViewModel: InsertItemsDonationViewModel
) {
    val context = LocalContext.current

    val insertItem by insertItemsDonationViewModel.insertItems.collectAsState(emptyList())
    val categories by insertItemsDonationViewModel.allCategories.collectAsState(emptyList())
    val stores by insertItemsDonationViewModel.allStores.collectAsState(emptyList())
    val isLoading by insertItemsDonationViewModel.isLoading.collectAsState(false)

    val selectedItem = remember { mutableStateOf<String?>(null) }
    var expandedDefaultStore by remember { mutableStateOf(false) }
    var defaultStore by remember { mutableStateOf<Stores?>(null) }

    //Add background image
    BackgroundImageElement()

    if (isLoading) {
        LoadIndicator(modifier)
    }

    Column(modifier = modifier.fillMaxSize()) {
        //Top
        TopBar(navController, stringResource(R.string.insert_donation_items_page_title))

        ExposedDropdownMenuBox(
            expanded = expandedDefaultStore,
            onExpandedChange = { expandedDefaultStore = it }
        ) {
            OutlinedTextfieldElement(
                value = defaultStore?.name ?: "",
                onValueChange = {},
                readOnly = true,
                labelText = stringResource(R.string.default_store),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedDefaultStore,
                onDismissRequest = { expandedDefaultStore = false }
            ) {
                stores.forEach { store ->
                    DropdownMenuItem(
                        text = { Text(store.name) },
                        onClick = {
                            defaultStore = store
                            expandedDefaultStore = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.size(UiConstants.itemSpacing))

        LazyColumn(Modifier.weight(1f)) {
            items(insertItem) { item ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = UiConstants.itemSpacing),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = { selectedItem.value = if (selectedItem.value == item.id) null else item.id }
                ) {
                    if (selectedItem.value != item.id) {
                        DisplayItem(item, insertItemsDonationViewModel)
                    } else {
                        EditItem(
                            item,
                            insertItemsDonationViewModel,
                            selectedItem,
                            categories,
                            stores,
                            defaultStore
                        )
                    }
                }
            }
        }

        Column(Modifier.fillMaxWidth()) {
            ButtonElement(
                text = stringResource(R.string.add_item),
                modifier = Modifier.fillMaxWidth(),
                onClick = { insertItemsDonationViewModel.addItem() }
            )

            Spacer(Modifier.size(UiConstants.itemSpacing))

            ButtonElement(
                text = stringResource(R.string.insert_stock),
                modifier = Modifier.fillMaxWidth(),
                onClick = { insertItemsDonationViewModel.addItemsToStock(selectedItem, defaultStore?.id, context) }
            )
        }
    }
}