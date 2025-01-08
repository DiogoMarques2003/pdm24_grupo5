package com.kotlin.socialstore.ui.elements.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DayCard(day: String, date: String, index: Int, selectedIndex: Int, onClick: () -> Unit) {

    val cardColor = if (index == selectedIndex) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .height(60.dp)
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date, modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}