package com.kotlin.socialstore.ui.elements

import UiConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.socialstore.R

@Composable
fun SubMenuCards(title: String, subTitle: String, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.size(80.dp, 110.dp).padding(6.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(title, fontSize = 25.sp)
            Text(subTitle)
            BackgroundImageElement()
        }
    }
}

@Composable
fun SubMenuSubmitVisit(onClick: () -> Unit){
    SubMenuCards(stringResource(R.string.submit_visits), stringResource(R.string.submit_visits_desc), onClick)
}

@Composable
fun SubMenuSchedules(onClick: () -> Unit){
    SubMenuCards(stringResource(R.string.schedules),stringResource(R.string.manage_schedules), onClick)
}

@Composable
fun SubMenuManageUser(onClick: () -> Unit){
    SubMenuCards(stringResource(R.string.manage_user),stringResource(R.string.manage_user_desc), onClick)
}

@Composable
fun SubMenuAddItemsUser(onClick: () -> Unit){
    SubMenuCards(stringResource(R.string.add_items),stringResource(R.string.add_items_desc), onClick)
}
