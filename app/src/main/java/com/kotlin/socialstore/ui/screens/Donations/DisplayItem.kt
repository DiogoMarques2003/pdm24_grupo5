package com.kotlin.socialstore.ui.screens.Donations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.viewModels.Donations.InsertItemsDonationViewModel

@Composable
fun DisplayItem(item: Stock, viewModel: InsertItemsDonationViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = item.picture ?: R.drawable.product_image_not_found,
                contentDescription = null,
                loading = { CircularProgressIndicator() },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(UiConstants.itemSpacing))

        // Item details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (!item.size.isNullOrEmpty()) {
                    Text(
                        text = "${stringResource(R.string.product_size)} ${item.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(UiConstants.itemSpacing))
        IconButton(onClick = { viewModel.removeItem(item.id) }) {
            Icon(Icons.Filled.Delete, "Delete")
        }
    }
}