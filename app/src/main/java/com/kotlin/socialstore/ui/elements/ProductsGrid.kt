package com.kotlin.socialstore.ui.elements

import DynamicColumnsGridList
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun ProductsGrid(stock: List<Stock>, categories: List<Category>) {
    val productID = remember { mutableStateOf<String?>(null) }

    DynamicColumnsGridList(
        items = stock,
        itemContent = { product ->
            val category = categories.find { it.id == product.categoryID }?.nome ?: ""
            ProductItemContent(product, category)

            if (productID.value == product.id) {
                ProductsPopUp(product, productID)
            }
        },
        onItemClick = { product ->
            productID.value = product.id
        },
        pictureProvider = { it.picture ?: R.drawable.product_image_not_found },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProductItemContent(product: Stock, category: String) {
    Text(category, fontWeight = FontWeight.Bold)

    if (!product.size.isNullOrEmpty()) {
        Text(stringResource(R.string.product_size) + product.size.toString())
    }

    Text(
        stringResource(R.string.product_state) + " " + stringResource(DataConstants.mapProductCondition[product.state]
            ?: R.string.product_state_default)
    )
}


@Composable
fun ProductCard(product: Stock, category: String, height: Dp, width: Dp, popUp: Boolean = true ){
    //Ui variables
    val productID = remember { mutableStateOf<String?>(null) }

    ElevatedCard(
        modifier = Modifier
            .size(height = height, width = width)
            .padding(UiConstants.itemSpacing),
        onClick = { productID.value = product.id }
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
        if (productID.value == product.id && popUp) {
            ProductsPopUp(product, productID)
        }
    }
}




