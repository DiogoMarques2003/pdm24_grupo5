import UiConstants.itemConditions
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    viewModel: StockViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var condition by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedStore by remember { mutableStateOf<Stores?>(null) }
    var selectedCondition by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedStore by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var storesID by remember { mutableStateOf("") }

    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val stores by viewModel.allStores.collectAsState(initial = emptyList())

    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                viewModel.uploadStockImage(it).collect { result ->
                    result.onSuccess { url ->
                        imageUri = url.toUri()
                        isUploading = false
                    }.onFailure { error ->
                        isUploading = false
                    }
                }
            }
        }
    }

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
                    Text(
                        "Add new item",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

//                    OutlinedCard(
//                        onClick = { launcher.launch("image/*") },
//                        modifier = Modifier
//                            .size(100.dp)
//                            .padding(bottom = 24.dp),
//                        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8F9FA)),
//                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
//                    ) {
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            if (imageUri != null) {
//                                Image(
//                                    painter = rememberAsyncImagePainter(imageUri),
//                                    contentDescription = null,
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop
//                                )
//                            } else {
//                                Icon(
//                                    Icons.Default.AddAPhoto,
//                                    contentDescription = null,
//                                    tint = Color.Gray,
//                                    modifier = Modifier.size(24.dp)
//                                )
//                            }
//                        }
//                    }


                    OutlinedTextfieldElement(
                        value = description,
                        onValueChange = { description = it },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextfieldElement(
                            value = size,
                            onValueChange = { size = it },
                            labelText = "Size",
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextfieldElement(
                            value = quantity,
                            onValueChange = { quantity = it.filter { char -> char.isDigit() } },
                            labelText = "Quantity",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    ItemConditionDropdown(
                        selectedCondition = selectedCondition,
                        onConditionSelected = { selectedCondition = it }
                    )

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

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

                    Spacer(Modifier.height(UiConstants.inputDialogSpacing))

                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator()
                            } else if (imageUri != null) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = "Selected photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { imageUri = null },
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
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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

                    ButtonElement(
                        text = "Add product",
                        onClick = {
                            selectedStore?.let { store ->
                                selectedCategory?.let { category ->
                                    viewModel.addStock(
                                        description = description,
                                        size = size,
                                        quantity = quantity.toIntOrNull() ?: 1,
                                        state = selectedCondition,
                                        categoryID = category.id,
                                        picture = imageUri?.toString(),
                                        storesId = store.id
                                    )
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        enabled = selectedCategory != null && selectedStore != null && description.isNotBlank() && selectedCondition.isNotBlank() && quantity.toIntOrNull()?.let { it > 0 } ?: false
                    )
                }
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDonationItemDialog(
    navController: NavController,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onPhotoSelected: (Uri) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf("") }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            onPhotoSelected(it)
        }
    }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Donation Item") },
        text = {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Item Name
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("What are you donating?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )

                // Condition Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Condition") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown arrow") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        itemConditions.forEach { condition ->
                            DropdownMenuItem(
                                text = { Text(condition) },
                                onClick = {
                                    selectedCondition = condition
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Size
                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Size (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )

                // Photo Upload Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentPhotoUri != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = rememberAsyncImagePainter(currentPhotoUri),
                                    contentDescription = "Selected photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { currentPhotoUri = null },
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
                                onClick = { photoPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (itemName.isNotBlank() && description.isNotBlank() && selectedCondition.isNotBlank()) {

                    }
                },
                enabled = itemName.isNotBlank() && description.isNotBlank() && selectedCondition.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



















