import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.viewModels.Products.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(
    item: Stock,
    viewModel: StockViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val stores by viewModel.allStores.collectAsState(initial = emptyList())

    var description by remember { mutableStateOf(item.description) }
    var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull() { it.id == item.categoryID }) }
    var size by remember { mutableStateOf(item.size ?: "") }
    var selectedCondition by remember { mutableStateOf(item.state) }
    var selectedStore by remember(stores) { mutableStateOf(stores.firstOrNull { it.id == item.storesId }) }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedStore by remember { mutableStateOf(false) }
    val context = LocalContext.current


    BasicAlertDialog(onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.92f),
        properties = DialogProperties(dismissOnClickOutside = true),
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Title
                    Text(
                        "Edit product",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Description
                    OutlinedTextfieldElement(
                        value = item.description,
                        onValueChange = { description = it },
                        labelText = "Description",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    // Category
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it }
                    ) {
                        OutlinedTextfieldElement(
                            value = selectedCategory?.nome ?: "",
                            onValueChange = {},
                            readOnly = true,
                            labelText = "Category",
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
                                        selectedCategory = category
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    OutlinedTextfieldElement(
                        value = size,
                        onValueChange = { size = it },
                        labelText = "Size",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    // Condition
                    ItemConditionDropdown(
                        selectedCondition = selectedCondition,
                        onConditionSelected = { selectedCondition = it }
                    )

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    // Store
                    ExposedDropdownMenuBox(
                        expanded = expandedStore,
                        onExpandedChange = { expandedStore = it }
                    ) {
                        OutlinedTextfieldElement(
                            value = selectedStore?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            labelText = "Store",
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
                                        selectedStore = store
                                        expandedStore = false
                                    }
                                )
                            }
                        }
                    }

                    // Edit product button
                    ButtonElement(
                        text = "Edit product",
                        onClick = {
                            try {
                                selectedStore?.let { store ->
                                    selectedCategory?.let { category ->
                                        val stock = mapOf(
                                            "description" to description,
                                            "size" to size,
                                            "state" to selectedCondition,
                                            "categoryId" to FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.category, category.id),
                                            "storesId" to store.id
                                        )
                                        viewModel.editProduct(item.id, stock)
                                        Toast.makeText(
                                            context,
                                            R.string.item_updated_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onDismiss()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    R.string.item_added_error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        enabled = selectedCategory != null && selectedStore != null && description.isNotBlank() && selectedCondition.isNotBlank() &&
                                (item.categoryID != selectedCategory!!.id ||
                                        item.size != size ||
                                        item.state != selectedCondition ||
                                        item.description != description ||
                                        item.storesId != selectedStore!!.id)

                    )
                }
            }
        }
    )
}
