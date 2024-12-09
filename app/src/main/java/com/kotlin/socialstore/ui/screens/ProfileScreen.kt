package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImage

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.pdmfinal),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Diogo Cláudia",
                style = TextStyle(fontSize = 24.sp, color = Color.Black)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Taken Items",
                style = TextStyle(fontSize = 18.sp, color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val items = listOf(
                "Hoodie - Quantity: 3",
                "Jeans - Quantity: 2",
                "T-Shirt - Quantity: 1"
            )
            items.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
        }
    }
}
