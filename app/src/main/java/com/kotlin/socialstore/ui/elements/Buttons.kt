package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonElement(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.size(50.dp),
        onClick = onClick,
        shape = RoundedCornerShape(35)
    ) {
        Text(text = text, fontSize = 15.sp)
    }
}