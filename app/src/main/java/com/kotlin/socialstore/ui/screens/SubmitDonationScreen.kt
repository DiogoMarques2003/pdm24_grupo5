//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import coil.compose.rememberAsyncImagePainter
//import android.net.Uri
//import androidx.compose.ui.text.style.TextAlign
//
//@Composable
//fun SubmitDonationPage(
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    viewModel: StockViewModel,
//    onSubmitDonations: (List<DonationItem>) -> Unit
//) {
//    var donationItems by remember { mutableStateOf(listOf<DonationItem>()) }
//    var showAddItemDialog by remember { mutableStateOf(false) }
//    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Donation Items",
//            style = MaterialTheme.typography.headlineMedium,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp),
//            textAlign = TextAlign.Center
//        )
//
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(donationItems, key = { it.id }) { item ->
//                DonationItemCard(
//                    item = item,
//                    onDelete = { donationItems = donationItems.filter { it.id != item.id } }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Button(
//                onClick = { showAddItemDialog = true },
//                modifier = Modifier.weight(1f)
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Item")
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Add Item")
//            }
//
//            Button(
//                onClick = {
//                    onSubmitDonations(donationItems)
//                    navController.navigate("donation_success")
//                },
//                enabled = donationItems.isNotEmpty(),
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Submit All")
//            }
//        }
//    }
//
//    if (showAddItemDialog) {
//        AddItemDialog(
//            viewModel = viewModel,
//            onDismiss = {},
//            modifier = modifier
//        )
//    }
//}
//
//@Composable
//fun DonationItemCard(
//    item: DonationItem,
//    onDelete: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (item.photoUri != null) {
//                Image(
//                    painter = rememberAsyncImagePainter(item.photoUri),
//                    contentDescription = "Item photo",
//                    modifier = Modifier
//                        .size(60.dp)
//                        .padding(end = 8.dp),
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = item.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = item.description,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Row {
//                    Text(
//                        text = item.condition,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    if (item.size.isNotBlank()) {
//                        Text(
//                            text = " • Size: ${item.size}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                    Text(
//                        text = " • Qty: ${item.quantity}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//
//            IconButton(onClick = onDelete) {
//                Icon(
//                    Icons.Default.Delete,
//                    contentDescription = "Delete item",
//                    tint = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}
