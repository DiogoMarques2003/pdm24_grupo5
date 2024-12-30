package com.kotlin.socialstore.ui.screens

import UiConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.ProductsCatalogViewModel

@Composable
fun ProductsCatalogPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    productsCatalogViewModel: ProductsCatalogViewModel
) {
    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            productsCatalogViewModel.stopListeners()
        }
    }

    //Get fresh data
    productsCatalogViewModel.getData(LocalContext.current)
    val allCategories by productsCatalogViewModel.allCategories.collectAsState(emptyList())
    val allProducts by productsCatalogViewModel.allStock.collectAsState(emptyList())

    Column(modifier = modifier.fillMaxSize())
    {
        //Top
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PopBackButton(navController)
            Spacer(Modifier.size(UiConstants.itemSpacing))
            Text("Products", fontSize = UiConstants.titleTextSize)
        }

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(allCategories) { item ->
                AssistChip(
                    onClick = { },
                    label = { Text(item.nome) },
                )
                Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
            }
        }
        Spacer(modifier = Modifier.size(UiConstants.itemSpacing))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(allProducts) { item ->
                ElevatedCard(modifier = Modifier.size(height = 150.dp, width = 150.dp)) {
                    Text(
                        allCategories.find { it.id == item.categoryID }?.nome ?: "",
                        fontWeight = FontWeight.Bold
                    )
                    if (item.picture != null) {
                        FirebaseObj.downloadImage(item.picture)
                    }
                    Text(item.description)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun Preview() {
    val productsCatalogViewModel = ProductsCatalogViewModel(LocalContext.current)
    ProductsCatalogPage(navController = rememberNavController(), Modifier, productsCatalogViewModel)
}