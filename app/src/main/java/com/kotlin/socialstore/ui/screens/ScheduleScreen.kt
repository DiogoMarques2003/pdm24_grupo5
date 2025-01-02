package com.kotlin.socialstore.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.viewModels.ScheduleViewModel
import localDateToDate
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.lazy.items

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SchedulePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    scheduleViewModel: ScheduleViewModel
) {
    //Data Variables
    val context = LocalContext.current
    val allSchedules by scheduleViewModel.allSchedules.collectAsState(emptyList())

    //Ui variables
    var currentdate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val monthName = currentdate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.titlecaseChar() }
    val firstDayWeek = currentdate.minusDays(currentdate.dayOfWeek.value.toLong() - 1)
    val weekDays = (0..6).map { firstDayWeek.plusDays(it.toLong()) }
    val selectedIndex = remember { mutableIntStateOf(1) }

    //get data
    LaunchedEffect(Unit) {
        scheduleViewModel.getSchedules(context)
    }

    //BackgroundImage
    BackgroundImageElement()

    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentdate = currentdate.minusWeeks(1)
            }) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft, contentDescription = null
                )
            }
            Text(
                monthName,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                currentdate = currentdate.plusWeeks(1)
            }) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight, contentDescription = null
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            LazyRow {
                itemsIndexed(items = weekDays) { index, date ->
                    val dayOfWeek =
                        date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).first()
                            .uppercase()
                    DayCard(
                        dayOfWeek, date.dayOfMonth.toString(), index, selectedIndex.intValue
                    ) {
                        selectedIndex.intValue = if (selectedIndex.intValue == index) -1 else index

                        allSchedules.filter {
                            val convData = localDateToDate(date)
                            it.day == convData
                        }
                    }
                }
            }
        }
        HorizontalDivider()
        LazyColumn {
            items(allSchedules) { item ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Text(item.day.toString())
                }
            }
        }
    }
}

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