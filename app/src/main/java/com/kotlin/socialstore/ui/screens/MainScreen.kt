package com.kotlin.socialstore.ui.screens

import ScrollableRowList
import androidx.compose.material3.Text
import UiConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.kotlin.socialstore.ui.elements.SubMenuAddItemsUser
import com.kotlin.socialstore.ui.elements.SubMenuManageUser
import com.kotlin.socialstore.ui.elements.SubMenuSchedules
import com.kotlin.socialstore.ui.elements.SubMenuSubmitVisit
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
    val visitsMonthlyData by mainPageViewModel.vistisMonthly.collectAsState(emptyList())
    val context = LocalContext.current

    var firstLastNameUser by remember { mutableStateOf("") }
    var visits by remember { mutableIntStateOf(0) }
    var takenItems by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        //Get fresh data
        mainPageViewModel.getUserInfo(context)
        mainPageViewModel.getDonations()
        mainPageViewModel.getVisits()
        mainPageViewModel.getProdcuts()
        mainPageViewModel.getTakenItems()
    }

    LaunchedEffect(userData) {
        if (userData != null) {
            val split = userData!!.name.split(" ")
            firstLastNameUser =
                if (split.first() == split.last()) split.first() else "${split.first()} ${split.last()}"
        }
    }

    //Background Image
    BackgroundImageElement()

    if (userData == null) {
        LoadIndicator()
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                        firstLastNameUser,
                        fontSize = UiConstants.titleTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(stringResource(R.string.check_last_news))
                }
            }
            Spacer(Modifier.size(UiConstants.defaultPadding))

            //Visits
            if (userData!!.accountType == DataConstants.AccountType.admin) {
                visits = visitsMonthlyData.size
            } else {
                if (!userData!!.familyHouseholdID.isNullOrEmpty()) {
                    visits = mainPageViewModel.getVisitsMonthlyById(userData!!.familyHouseholdID!!)
                        .collectAsState(
                            emptyList()
                        ).value.size
                }
            }

            //Taken Items
            if (userData!!.accountType != DataConstants.AccountType.benefiaryy) {
                takenItems =
                    mainPageViewModel.getTakenItensMonthly().collectAsState(null).value ?: 0
            } else {
                if (!userData!!.familyHouseholdID.isNullOrEmpty()) {
                    takenItems =
                        mainPageViewModel.getTakenItensMonthlyById(userData!!.familyHouseholdID!!)
                            .collectAsState(null).value ?: 0
                }
            }

            Column(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.this_month))
                Text(
                    stringResource(R.string.visits) + ": " + visits,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (userData!!.accountType != DataConstants.AccountType.benefiaryy)
                        stringResource(R.string.taken_items_text_admin) + ": " + takenItems
                    else
                        stringResource(R.string.taken_items_text) + ": " + takenItems,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.size(UiConstants.defaultPadding))

            //Draw ADMIN menus
            if (userData!!.accountType != DataConstants.AccountType.benefiaryy) {
                Column {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        item {
                            SubMenuSubmitVisit { navController.navigate("qrcode_reader_screen/check_in") }
                        }
                        item {
                            SubMenuSchedules { navController.navigate("schedule_screen") }
                        }
                        item {
                            SubMenuAddItemsUser { navController.navigate("qrcode_reader_screen/checkout") }
                        }
                        item {
                            if (userData!!.accountType == DataConstants.AccountType.admin) {
                                SubMenuManageUser { navController.navigate("manage_users") }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.size(UiConstants.itemSpacing))
            if (userData!!.accountType != DataConstants.AccountType.admin) {
                Text(
                    stringResource(R.string.products_page_title),
                    Modifier.clickable { navController.navigate("products_screen") },
                    fontSize = UiConstants.titleTextSize
                )
                ScrollableRowList(
                    items = productsData,
                    itemContent = { product ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = (categoriesData.find { it.id == product.categoryID }?.nome
                                    ?: "").toString(),
                                fontWeight = FontWeight.Bold
                            )
                            if (!product.size.isNullOrEmpty()) {
                                Text(
                                    text = stringResource(R.string.product_size) + product.size.toString()
                                )
                            }
                            Text(
                                text = stringResource(R.string.product_state) + " " + stringResource(
                                    DataConstants.mapProductCondition[product.state]
                                        ?: R.string.product_state_default
                                )
                            )
                        }
                    },
                    pictureProvider = { it.picture ?: R.drawable.product_image_not_found },
                    onItemClick = { }
                )
            }

            Spacer(Modifier.size(UiConstants.defaultPadding))
            if (userData!!.accountType == DataConstants.AccountType.benefiaryy) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("submit_donation")
                        }) {
                    Text(
                        stringResource(R.string.donate),
                        fontSize = UiConstants.titleTextSize
                    )
                    Text(
                        stringResource(R.string.donate_desc)
                    )
                }
            } else {
                Text(
                    stringResource(R.string.donations),
                    Modifier.clickable { navController.navigate("list_donations_screen") },
                    fontSize = UiConstants.titleTextSize
                )
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(donationsData) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .size(height = 150.dp, width = 150.dp)
                                .padding(UiConstants.itemSpacing)
                                .clickable {
                                    navController.navigate("donation_screen/${item.id}")
                                }
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(6.dp)
                            ) {
                                Text("#" + item.donationId, fontSize = UiConstants.titleTextSize)
                                Text(mainPageViewModel.getPublishDays(item.creationDate,
                                    LocalContext.current))
                            }
                        }
                    }
                }
                Spacer(Modifier.size(UiConstants.itemSpacing))
                if (userData!!.accountType == DataConstants.AccountType.admin) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("manage_stock")
                            }) {
                        Text(
                            stringResource(R.string.manageStock_Title),
                            fontSize = UiConstants.titleTextSize
                        )
                        Text(
                            stringResource(R.string.manage_stock_desc)
                        )
                    }
                }
            }
        }
    }
}