package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.R

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun BackgroundImage () {
    val density = LocalDensity.current
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.social_store_image_no_background),
            contentDescription = "Image",
            modifier = Modifier.fillMaxSize()
            .align(Alignment.BottomCenter)
                .graphicsLayer(rotationZ = -21F)
                .offset(x = -18.dp, y = 350.dp),
            contentScale = ContentScale.Fit,
            alpha = 0.25F, // Opacity
        )
    }
}