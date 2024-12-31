package com.kotlin.socialstore.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import UiConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.ProductCard
import com.kotlin.socialstore.viewModels.MainPageViewModel

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    mainPageViewModel: MainPageViewModel
) {
    val userData by mainPageViewModel.userData.collectAsState(null)
    val productsData by mainPageViewModel.lastProducts.collectAsState(emptyList())
    val categoriesData by mainPageViewModel.allCategories.collectAsState(emptyList())
    val donationsData by mainPageViewModel.lastDonations.collectAsState(emptyList())
    val visitsData by mainPageViewModel.lastVisits.collectAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        //Get fresh data
        mainPageViewModel.getUserInfo(context)
        mainPageViewModel.getDonations(context)
        mainPageViewModel.getVisits(context)
    }

    //Background Image
    BackgroundImageElement()

    if (userData == null) {
        LoadIndicator()
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Spacer(Modifier.size(UiConstants.itemSpacing))
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
                        userData!!.name,
                        fontSize = UiConstants.titleTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Check our latest news!")
                }
            }
            Spacer(Modifier.size(UiConstants.defaultPadding))
            Column(Modifier.fillMaxWidth()) {
                Text("This Month")
                Text("Visits: " + visitsData.size , fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text("Taken Items: ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

            //Draw ADMIN menus
            if (userData!!.accountType != DataConstants.AccountType.benefiaryy) {
                Column(verticalArrangement = Arrangement.SpaceEvenly) {
                    Spacer(Modifier.size(UiConstants.defaultPadding))
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.clickable {
                            navController.navigate("qrcode_reader_screen/profile_screen")
                        }) {
                            Text("Submit visit", fontSize = UiConstants.titleTextSize)
                            Text("Scan QrCode")
                        }
                        Spacer(Modifier.size(UiConstants.defaultPadding))
                        Column(Modifier.clickable {}) {
                            Text("Schedules", fontSize = UiConstants.titleTextSize)
                            Text("Manage schedules")
                        }
                    }
                    Spacer(Modifier.size(UiConstants.defaultPadding))
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.clickable {}) {
                            Text("Manage user", fontSize = UiConstants.titleTextSize)
                            Text("Edit user profiles")
                        }
                        Spacer(Modifier.size(UiConstants.defaultPadding))
                        Column(Modifier.clickable {}) {
                            Text("Add items", fontSize = UiConstants.titleTextSize)
                            Text("Scan QrCode")
                        }
                    }
                }
            }

            Spacer(Modifier.size(UiConstants.defaultPadding))
            Text(
                "Products",
                Modifier.clickable { navController.navigate("products_screen") },
                fontSize = UiConstants.titleTextSize
            )
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(productsData) { item ->
                    ProductCard(
                        item,
                        (categoriesData.find { it.id == item.categoryID }?.nome ?: "").toString(),
                        200.dp,
                        150.dp,
                        false
                    )
                }
            }
            Spacer(Modifier.size(UiConstants.defaultPadding))
            if (userData!!.accountType == DataConstants.AccountType.benefiaryy) {
                Text("Donations")
            } else {
                Text(
                    "Donations",
                    Modifier.clickable { navController.navigate("main_screen") },
                    fontSize = UiConstants.titleTextSize
                )
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(donationsData) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .size(height = 300.dp, width = 140.dp)
                                .padding(UiConstants.itemSpacing)
                        ) {
                            Column(Modifier.fillMaxSize().padding(6.dp)) {
                                Text("Donation " + "#" +item.donationId)
                                Text("Date: " + item.creationDate)
                            }
                        }
                    }
                }
            }
        }
    }
}