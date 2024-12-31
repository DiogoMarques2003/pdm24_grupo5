package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
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
    val showQrCodePopup = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.getUserInfo(context)
    }

    if (userInfo == null) {
        CircularProgressIndicator()
    } else {

        Box(modifier = modifier.fillMaxSize()) {
            // Fundo
            BackgroundImageElement()

            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.weight(1f)) {
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

                Spacer(modifier = Modifier.height(5.dp))

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
                    onClick = { /* Navegar para edição */ },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = stringResource(R.string.edit_profile_button),
                )

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

                    IconButton(onClick = { /* Navegar para gerenciamento de Household */ }) {
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

                //              Spacer(modifier = Modifier.height(16.dp))

                // Lista de itens
                val items = listOf(
                    "Hoodie - Quantity: 3",
                    "Jeans - Quantity: 2",
                    "T-Shirt - Quantity: 1"
                )

                items.forEach { item ->
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFBBDEFB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.first().toString(),
                                    style = TextStyle(color = Color.White, fontSize = 18.sp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = item.substringBefore(" - "),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = item.substringAfter(" - "),
                                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                                )
                            }
                        }
                    }
                }

                //        Spacer(modifier = Modifier.height(24.dp))

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