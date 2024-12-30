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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.screens.ProductsPopUp

@Composable
fun ProductsGrid(stock: List<Stock>, categories: List<Category>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(stock) { item ->
            //Ui variables
            val showProductPopUp = remember { mutableStateOf(false) }

            ElevatedCard(
                modifier = Modifier
                    .size(height = 200.dp, width = 170.dp)
                    .padding(UiConstants.itemSpacing),
                onClick = { showProductPopUp.value = !showProductPopUp.value }
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    SubcomposeAsyncImage(
                        model = item.picture ?: R.drawable.product_image_not_found,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(modifier = Modifier.padding(5.dp)) {
                    Text(
                        categories.find { it.id == item.categoryID }?.nome ?: "",
                        fontWeight = FontWeight.Bold
                    )
                    if (item.size != null) {
                        Text(stringResource(R.string.product_size) + item.size.toString())
                    }
                    Text(
                        stringResource(R.string.product_state) + " " + stringResource(
                            DataConstants.mapProductCondition[item.state]
                                ?: R.string.product_state_default
                        )
                    )
                }
                if (showProductPopUp.value) {
                    ProductsPopUp(item, showProductPopUp)
                }

            }
        }
    }
}