package com.kotlin.socialstore.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun RadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text
        )
    }
}