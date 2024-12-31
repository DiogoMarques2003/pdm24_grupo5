package com.kotlin.socialstore.ui.screens

import UiConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.repository.UsersRepository
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.viewModels.MainPageViewModel

@Composable
fun MainScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    mainPageViewModel: MainPageViewModel
) {
    val userData by mainPageViewModel.userData.collectAsState(null)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        //Get fresh data
        mainPageViewModel.getUserInfo(context)
    }

    //Background Image
    BackgroundImageElement()

    if (userData == null) {
        LoadIndicator()
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Spacer(Modifier.size(UiConstants.itemSpacing))
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clickable { navController.navigate("profile_page_screen") },) {
                SubcomposeAsyncImage(
                    model = userData?.profilePic ?: R.drawable.product_image_not_found,
                    contentDescription = null,
                    loading = { CircularProgressIndicator() },
                    modifier = Modifier.clip(CircleShape).size(80.dp).align(Alignment.CenterVertically),
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
                    Text(userData!!.name)
                }
            }
            Spacer(Modifier.size(UiConstants.defaultPadding))
            Column {
                Text("This Month")
                Row(Modifier.fillMaxWidth()) {
                    Text("Visits: ")
                }
            }
        }
    }
}