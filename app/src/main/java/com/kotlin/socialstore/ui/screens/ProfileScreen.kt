package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.QrCodePopup
import com.kotlin.socialstore.viewModels.LoginViewModel
import com.kotlin.socialstore.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel
) {
    val userInfo by profileViewModel.userData.collectAsState(null)
    val takenItems by profileViewModel.takenItems.collectAsState(emptyList())
    val categories by profileViewModel.categories.collectAsState(emptyList())
    val showQrCodePopup = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo(context)
    }

    if (userInfo == null) {
        LoadIndicator()
    } else {

        Box(modifier = modifier.fillMaxSize()) {

            BackgroundImageElement()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier) {
                    SubcomposeAsyncImage(
                        model = userInfo?.profilePic ?: R.drawable.product_image_not_found,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // Nome do usuário
                Text(
                    text = userInfo!!.name,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                //  "Edit Profile"
                ButtonElement(
                    onClick = { navController.navigate("edit_profile_screen") },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = stringResource(R.string.edit_profile_button),
                )

                if (userInfo!!.accountType == DataConstants.AccountType.benefiaryy) {


                    Spacer(modifier = Modifier.height(5.dp))

                    // "Household"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Household",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Text(
                                text = "Manage your household",
                                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                            )
                        }

                        IconButton(onClick = { navController.navigate("manage_household") }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Arrow Forward"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    //  "Taken Items"
                    Text(
                        text = "Taken Items",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )


                    LazyColumn(Modifier.weight(1f)) {
                        items(takenItems) { item ->
                            val categoryName =
                                categories.firstOrNull { it.id == item.categoryID }?.nome ?: "N/A"

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE3F2FD)
                                ),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.elevatedCardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = categoryName,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        )
                                        Text(
                                            text = "Quantity: ${item.quantity}",
                                            style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Botão QR Code
                    Button(
                        onClick = { showQrCodePopup.value = true },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(text = "QR Code")
                    }

                }
            }

            if (showQrCodePopup.value) {
                QrCodePopup(userInfo!!.id, showQrCodePopup)
            }
        }
    }
}

