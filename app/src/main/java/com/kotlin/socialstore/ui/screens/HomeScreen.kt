package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.ButtonElement

//DEFAULT VALUES
private val itemSpacing = 8.dp
private val imageContentSpacing = 40.dp

@Composable
fun HomePage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(R.drawable.social_store_image_no_background),
            contentDescription = "Image",
            modifier = Modifier.size(340.dp)
        )
        Text(text = stringResource(R.string.homepage_welcome),
            fontSize = 50.sp)
        Column {
            ButtonElement(
                text = stringResource(R.string.login_button),
                onClick = { navController.navigate("login_screen") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(itemSpacing))
            ButtonElement(
                text = "Register",
                onClick = { navController.navigate("register_screen") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(itemSpacing))
            ButtonElement(
                text = stringResource(R.string.homepage_Donate),
                onClick = { navController.navigate("submit_donation") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(itemSpacing))
            
            ButtonElement(
                text = stringResource(R.string.homepage_AddItem),
                onClick = { navController.navigate("manage_stock") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}