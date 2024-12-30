package com.kotlin.socialstore.ui.screens

import UiConstants
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.ui.elements.ProductsGrid
import com.kotlin.socialstore.viewModels.ProductsCatalogViewModel

@Composable
fun ProductsCatalogPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    productsCatalogViewModel: ProductsCatalogViewModel
) {
    //Variables
    val allCategories by productsCatalogViewModel.allCategories.collectAsState(emptyList())
    val allProducts by productsCatalogViewModel.allStock.collectAsState(emptyList())
    val context = LocalContext.current

    //Ui variables
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var displayProducts by remember { mutableStateOf<List<Stock>>(emptyList()) }

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            productsCatalogViewModel.stopListeners()
        }
    }

    LaunchedEffect(Unit) {
        //Get fresh data
        productsCatalogViewModel.getData(context)
    }

    LaunchedEffect(selectedCategory, allProducts) {
        displayProducts =
            if (selectedCategory == null) allProducts else allProducts.filter { it.categoryID == selectedCategory }
        Log.w("","Category: $selectedCategory")
    }

    //Add background image
    BackgroundImageElement()

    Column(modifier = modifier.fillMaxSize())
    {
        //Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PopBackButton(navController)
            Spacer(Modifier.size(UiConstants.itemSpacing))
            Text(stringResource(R.string.products_page_title), fontSize = UiConstants.titleTextSize)
        }

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(allCategories) { item ->
                AssistChip(
                    onClick = {
                        selectedCategory = if (selectedCategory == item.id) null else item.id
                    },
                    label = { Text(item.nome) },
                )
                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
            }
        }
        Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
        if (displayProducts.isEmpty()) {
            Text("Produtos não encontrados")
        } else {
            ProductsGrid(displayProducts,allCategories)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun Preview() {
    val productsCatalogViewModel = ProductsCatalogViewModel(LocalContext.current)
    ProductsCatalogPage(navController = rememberNavController(), Modifier, productsCatalogViewModel)
}