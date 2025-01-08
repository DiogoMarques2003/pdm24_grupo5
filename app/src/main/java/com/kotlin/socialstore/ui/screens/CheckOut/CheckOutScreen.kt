package com.kotlin.socialstore.ui.screens.Products

import CheckOutViewModel
import TopBar
import TwoColumnGridList
import UiConstants
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.ui.elements.ProductItemContent
import com.kotlin.socialstore.ui.elements.ProductsGrid
import com.kotlin.socialstore.viewModels.Products.ProductsCatalogViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun CheckOutScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CheckOutViewModel
) {
    var selectedItems = viewModel.selectedItems.collectAsState()
    val userData by viewModel.userData.collectAsState(null)
    val allCategories by viewModel.allCategories.collectAsState(emptyList())
    val allProducts by viewModel.allStock.collectAsState(emptyList())
    val context = LocalContext.current
    val isConfirmEnabled by viewModel.isConfirmEnabled.collectAsState(false)

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var displayProducts by remember { mutableStateOf<List<Stock>>(emptyList()) }

    var firstLastNameUser by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getData(context)
    }

    LaunchedEffect(selectedCategory, allProducts) {
        displayProducts =
            if (selectedCategory == null) allProducts else allProducts.filter { it.categoryID == selectedCategory }
        Log.w("", "Category: $selectedCategory")
    }

    LaunchedEffect(userData) {
        if (userData != null) {
            val split = userData!!.name.split(" ")
            firstLastNameUser =
                if (split.first() == split.last()) split.first() else "${split.first()} ${split.last()}"
        }
    }

    BackgroundImageElement()

    Column(modifier = modifier.fillMaxSize())
    {
        TopBar(title = "Add items user", navController = navController)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clickable { navController.navigate("profile_page_screen") },
        ) {
            SubcomposeAsyncImage(
                model = userData?.profilePic ?: R.drawable.product_image_not_found,
                contentDescription = null,
                loading = { CircularProgressIndicator() },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.size(UiConstants.defaultPadding))
            Column {
                Spacer(Modifier.size(UiConstants.itemSpacing))
                Text(
                    firstLastNameUser,
                    fontSize = UiConstants.titleTextSize,
                    fontWeight = FontWeight.Bold
                )
                Text("Adding items to Household")
            }
        }

        Spacer(Modifier.size(UiConstants.defaultPadding))

        LazyRow(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
            items(allCategories) { item ->
                AssistChip(
                    onClick = {
                        selectedCategory = if (selectedCategory == item.id) null else item.id
                    },
                    label = { Text(item.nome) }
                )

                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
            }
        }


        if (displayProducts.isEmpty()) {
            Text("Produtos não encontrados")
        } else {
            val productID = remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(modifier = modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        TwoColumnGridList(
                            items = displayProducts,
                            itemContent = { product ->
                                val category = allCategories.find { it.id == product.categoryID }?.nome ?: ""
                                ProductItemContent(product, category)

                                if (productID.value == product.id) {
                                    ProductsPopUp(product, productID)
                                }
                            },
                            onItemClick = { product ->
                                productID.value = product.id
                            },
                            pictureProvider = { it.picture ?: R.drawable.product_image_not_found },
                            modifier = Modifier.fillMaxSize(),
                            columns = 3,
                            showAddButton = true,
                            onAddButtonClick = { item ->
                                viewModel.toggleItemSelection(item.id)
                            },
                            isItemSelected = { item ->
                                selectedItems.value.contains(item.id)
                            }
                        )
                    }

                    ButtonElement(
                        onClick = {viewModel.onCheckOut() },
                        text = "Confirm",
                        enabled = isConfirmEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

        }
    }
}