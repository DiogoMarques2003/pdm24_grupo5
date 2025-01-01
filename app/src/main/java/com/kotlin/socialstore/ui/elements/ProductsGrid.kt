package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.screens.Products.ProductsPopUp

@Composable
fun ProductsGrid(stock: List<Stock>, categories: List<Category>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(stock) { item ->
            ProductCard(item, (categories.find { it.id == item.categoryID }?.nome ?: "").toString(), 200.dp,200.dp)
        }
    }
}

@Composable
fun ProductCard(product: Stock, category: String, height: Dp, width: Dp, popUp: Boolean = true ){
    //Ui variables
    val showProductPopUp = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .size(height = height, width = width)
            .padding(UiConstants.itemSpacing),
        onClick = { showProductPopUp.value = !showProductPopUp.value }
    ) {
        Row(modifier = Modifier.weight(1f)) {
            SubcomposeAsyncImage(
                model = product.picture ?: R.drawable.product_image_not_found,
                contentDescription = null,
                loading = { CircularProgressIndicator() },
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                category,
                fontWeight = FontWeight.Bold
            )
            if (!product.size.isNullOrEmpty()) {
                Text(stringResource(R.string.product_size) + product.size.toString())
            }
            Text(
                stringResource(R.string.product_state) + " " + stringResource(
                    DataConstants.mapProductCondition[product.state]
                        ?: R.string.product_state_default
                )
            )
        }
        if (showProductPopUp.value && popUp) {
            ProductsPopUp(product, showProductPopUp)
        }
    }
}