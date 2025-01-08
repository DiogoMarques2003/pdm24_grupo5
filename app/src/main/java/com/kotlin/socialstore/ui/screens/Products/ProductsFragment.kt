package com.kotlin.socialstore.ui.screens.Products

import UiConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@Composable
fun ProductsPopUp(stock: Stock, productClicked: MutableState<String?>) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(UiConstants.defaultPadding)) {
                Row(modifier = Modifier.weight(1f)) {
                    AsyncImage(
                        model = stock.picture ?: R.drawable.product_image_not_found ,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.size(UiConstants.itemSpacing))
                OutlinedTextfieldElement(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {},
                    value = stock.description,
                    labelText = "Description",
                    leadingIcon = null,
                    trailingIcon = {}
                )
                Spacer(Modifier.size(UiConstants.itemSpacing))
                ButtonElement("Back", { productClicked.value = null }, Modifier.fillMaxWidth())
            }
        }
    }
}
