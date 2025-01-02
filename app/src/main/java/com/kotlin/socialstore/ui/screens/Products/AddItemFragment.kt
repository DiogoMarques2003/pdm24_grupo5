import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.viewModels.Products.StockViewModel
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
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedStore by remember { mutableStateOf<Stores?>(null) }
    var selectedCondition by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedStore by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var filePath by remember { mutableStateOf("") }

    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val stores by viewModel.allStores.collectAsState(initial = emptyList())

    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                viewModel.uploadStockImage(it).collect { result ->
                    result.onSuccess { url ->
                        imageUri = FirebaseObj.getImageUrl(url)?.toUri()
                        isUploading = false
                        filePath = url
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

                    Spacer(Modifier.height(UiConstants.imageContentSpacing))

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
                            try {
                                selectedStore?.let { store ->
                                    selectedCategory?.let { category ->
                                        viewModel.addStock(
                                            description = description,
                                            size = size,
                                            quantity = quantity.toIntOrNull() ?: 1,
                                            state = selectedCondition,
                                            categoryID = category.id,
                                            picture = filePath,
                                            storesId = store.id
                                        )
                                        Toast.makeText(
                                            context,
                                            R.string.item_added_success,
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
                        enabled = selectedCategory != null && selectedStore != null && description.isNotBlank() && selectedCondition.isNotBlank() && quantity.toIntOrNull()?.let { it > 0 } ?: false
                    )
                }
            }
        }
    )
}






