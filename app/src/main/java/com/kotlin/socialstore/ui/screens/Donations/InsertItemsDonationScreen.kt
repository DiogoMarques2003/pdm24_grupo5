package com.kotlin.socialstore.ui.screens.Donations

import ImageUploader
import TopBar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.viewModels.Donations.InsertItemsDonationViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.data.firebase.FirebaseObj
import kotlinx.coroutines.launch

@Composable
fun InsertItemsDonationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    insertItemsDonationViewModel: InsertItemsDonationViewModel
) {
    val insertItem by insertItemsDonationViewModel.insertItems.collectAsState(emptyList())

    var selectedItem by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                insertItemsDonationViewModel.uploadDonationImage(it).collect { result ->
                    result.onSuccess { url ->
                        insertItemsDonationViewModel.updateItemImage(selectedItem!!, url)
                        isUploading = false
                    }.onFailure { error ->
                        isUploading = false
                    }
                }
            }
        }
    }

    //Add background image
    BackgroundImageElement()

    Column(modifier = modifier.fillMaxSize()) {
        //Top
        TopBar(navController, stringResource(R.string.insert_donation_items_page_title))

        LazyColumn(Modifier.fillMaxSize()) {
            items(insertItem) { item ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = UiConstants.itemSpacing),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = { selectedItem = if (selectedItem == item.id) null else item.id }
                ) {
                    if (selectedItem != item.id) {
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
                                            text = "${stringResource(R.string.product_size)} ${item.size} - ",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Card(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                                ),
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
                                    } else if (item.picture != null) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            AsyncImage(
                                                model = item.picture,
                                                contentDescription = "Selected photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            IconButton(
                                                onClick = { item.picture = null },
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
                                                containerColor = MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.1f
                                                )
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.AddAPhoto,
                                                contentDescription = "Add photo",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            androidx.compose.material3.Text(
                                                "Add Photo",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}