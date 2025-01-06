package com.kotlin.socialstore.ui.screens.Donations

import ItemConditionDropdown
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.viewModels.Donations.InsertItemsDonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItem(
    item: Stock,
    viewModel: InsertItemsDonationViewModel,
    selectedItem: MutableState<String?>,
    categories: List<Category>,
    stores: List<Stores>,
    defaultStore: Stores?
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateItemField(selectedItem.value!!, "picture", uri.toString())
        }
    }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedStore by remember { mutableStateOf(false) }

    val selectedCategory = categories.firstOrNull { it.id == item.categoryID }
    val selectedStore = stores.firstOrNull { it.id == item.storesId }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (item.picture != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = item.picture,
                            contentDescription = "Selected photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.updateItemField(selectedItem.value!!, "picture", null) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            )
                        )
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Add photo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Add Photo",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        OutlinedTextfieldElement(
            value = item.description,
            onValueChange = { viewModel.updateItemField(selectedItem.value!!, "description", it) },
            labelText = "Description",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it }
        ) {
            OutlinedTextfieldElement(
                value = selectedCategory?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                labelText = stringResource(R.string.category),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.nome) },
                        onClick = {
                            item.categoryID = category.id
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        OutlinedTextfieldElement(
            value = item.size ?: "",
            onValueChange = { viewModel.updateItemField(selectedItem.value!!, "size", it) },
            labelText = stringResource(R.string.product_size),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(UiConstants.inputDialogSpacing))

        ItemConditionDropdown(
            selectedCondition = item.state,
            onConditionSelected = { viewModel.updateItemField(selectedItem.value!!, "state", it) }
        )

        ExposedDropdownMenuBox(
            expanded = expandedStore,
            onExpandedChange = { expandedStore = it }
        ) {
            OutlinedTextfieldElement(
                value = selectedStore?.name ?: (defaultStore?.name ?: ""),
                onValueChange = {},
                readOnly = true,
                labelText = stringResource(R.string.store),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedStore,
                onDismissRequest = { expandedStore = false }
            ) {
                stores.forEach { store ->
                    DropdownMenuItem(
                        text = { Text(store.name) },
                        onClick = {
                            item.storesId = store.id
                            expandedStore = false
                        }
                    )
                }
            }
        }
    }
}