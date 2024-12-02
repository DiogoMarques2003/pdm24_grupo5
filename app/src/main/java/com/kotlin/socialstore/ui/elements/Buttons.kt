package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun buttonElement(text: String, color: Color, onClick: () -> Unit) {
    Button(modifier = Modifier.size(width = 20.dp, height = 20.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(color),
        shape = RoundedCornerShape(35)
    ) {
        Text(text = text, fontSize = 15.sp)
    }
}