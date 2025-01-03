package com.kotlin.socialstore.ui.screens

import UiConstants
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.DialogProperties
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import com.kotlin.socialstore.ui.elements.DatePickerElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.TimePickerWithDialog
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

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
    val allVolunteers by scheduleViewModel.allVolunteers.collectAsState(emptyList())

    //Ui variables
    var currentdate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val monthName = currentdate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.titlecaseChar() }
    val firstDayWeek = currentdate.minusDays(currentdate.dayOfWeek.value.toLong() - 1)
    val weekDays = (0..6).map { firstDayWeek.plusDays(it.toLong()) }
    val selectedIndex = remember { mutableIntStateOf(weekDays.indexOf(LocalDate.now())) }
    var filteredSchedules by remember { mutableStateOf(emptyList<VolunteerSchedule>()) }
    var selectedData by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    //get data
    LaunchedEffect(Unit) {
        scheduleViewModel.getSchedules(context)
        scheduleViewModel.getVolunteers()
    }

    LaunchedEffect(selectedData, allSchedules) {
        val dateConv = localDateToDate(selectedData)

        filteredSchedules = allSchedules.filter {
            it.day.date == dateConv.date
        }
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
                selectedData = selectedData.minusWeeks(1)
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
                selectedData = selectedData.plusWeeks(1)
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
                        selectedData = date
                    }
                }
            }
        }
        HorizontalDivider()
        Spacer(Modifier.size(UiConstants.defaultPadding))
        LazyColumn() {
            items(filteredSchedules) { item ->
                val vol = allVolunteers.firstOrNull() { it.id == item.userID }

                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(10.dp)) {
                        Row {
                            SubcomposeAsyncImage(
                                model = vol?.profilePic ?: R.drawable.product_image_not_found,
                                contentDescription = null,
                                loading = { CircularProgressIndicator() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(80.dp)
                                    .align(Alignment.CenterVertically),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.size(UiConstants.itemSpacing))
                            Column {
                                if (vol != null) {
                                    Text(vol.name)
                                }
                                Text(item.workFunction ?: "N/A")
                                Text("Local: " + item.localId.toString())
                                Text(item.startTime + " ás " + item.endTime)
                            }
                        }
                    }
                }
            }
        }
    }
    Box(modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(65.dp)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }

        if (showDialog) {
            AddScheduleDialog(onDissmiss = {
                showDialog = false
            },
                dropDown = {
                    Button(onClick = {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            allVolunteers.forEach { option ->
                                DropdownMenuItem(
                                    text  = { Text(option.name) },
                                    onClick = { /* Do something... */ }
                                )
                            }
                        }
                    } ) { Text("Test") }
                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(onDissmiss: () -> Unit, dropDown: @Composable () -> Unit) {
    var text by remember { mutableStateOf("Hello") }
    val selectedDate = remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDissmiss,
        modifier = Modifier.fillMaxWidth(0.92f),
        properties = DialogProperties(dismissOnClickOutside = true),
        content = {
            Card(Modifier.fillMaxWidth()) {
                dropDown()
                DatePickerElement(selectedDate)
                TimePickerWithDialog()
            }
        }
    )
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