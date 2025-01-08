package com.kotlin.socialstore.ui.screens

import RowList
import TopBar
import UiConstants
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.res.stringResource
import com.google.firebase.firestore.auth.User
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import com.kotlin.socialstore.ui.elements.cards.DayCard
import com.kotlin.socialstore.ui.elements.dialogs.AddScheduleDialog
import com.kotlin.socialstore.ui.elements.dialogs.WorkFunctionDialog
import convertStringToDate

@OptIn(ExperimentalMaterial3Api::class)
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
    val allStores by scheduleViewModel.allStores.collectAsState(emptyList())
    val currUser by scheduleViewModel.currUser.collectAsState(null)

    //Ui variables
    var currentdate by remember { mutableStateOf(LocalDate.now()) }
    val monthName = currentdate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.titlecaseChar() }
    val firstDayWeek = currentdate.minusDays(currentdate.dayOfWeek.value.toLong() - 1)
    val weekDays = (0..6).map { firstDayWeek.plusDays(it.toLong()) }
    val selectedIndex = remember { mutableIntStateOf(weekDays.indexOf(LocalDate.now())) }
    var filteredSchedules by remember { mutableStateOf(emptyList<VolunteerSchedule>()) }
    var selectedData by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var showDialog by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("") }
    val selectedInTime = remember { mutableStateOf("") }
    val selectedOutTime = remember { mutableStateOf("") }
    val expandedVol = remember { mutableStateOf(false) }
    val expandedStore = remember { mutableStateOf(false) }
    val selectedVol = remember { mutableStateOf("") }
    val selectedVolId = remember { mutableStateOf("") }
    val selectedStore = remember { mutableStateOf("") }
    val selectedStoreId = remember { mutableStateOf("") }
    val selectedWorkFunction = remember { mutableStateOf("") }

    //get data
    LaunchedEffect(Unit) {
        scheduleViewModel.getSchedules(context)
        scheduleViewModel.getVolunteers()
    }

    LaunchedEffect(selectedData, allSchedules) {
        val dateConv = localDateToDate(selectedData)

        filteredSchedules = allSchedules.filter {
            it.day.date == dateConv.date &&
                    it.day.month == dateConv.month &&
                    it.day.year == dateConv.year
        }
    }

    val dateConv = localDateToDate(selectedData)
    filteredSchedules = allSchedules.filter {
        it.day.date == dateConv.date &&
                it.day.month == dateConv.month &&
                it.day.year == dateConv.year
    }

    //BackgroundImage
    BackgroundImageElement()

    Column(modifier.fillMaxSize()) {
        TopBar(navController, stringResource(R.string.schedule_title))
        HorizontalDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Start),
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
                "$monthName ${currentdate.year}",
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
        RowList(items = filteredSchedules, itemContent = { item ->
            val vol = allVolunteers.firstOrNull() { it.id == item.userID }
            val storeName = allStores.firstOrNull { it.id == item.localId }?.name
            val stateText =
                if (item.accepted) stringResource(R.string.donation_approved) else stringResource(
                    R.string.donation_pending
                )
            if (vol != null) {
                Text(vol.name, fontWeight = FontWeight.Bold)
            }
            if (!item.workFunction.isNullOrEmpty()) {
                Text("${stringResource(R.string.function)}: ${item.workFunction}")
            }
            if (!storeName.isNullOrEmpty()) {
                Text("${stringResource(R.string.store)}: $storeName")
            }
            Text("${item.startTime} ${stringResource(R.string.to)} ${item.endTime}")
            Text("${stringResource(R.string.product_state)} $stateText")
        },
            itemEndContet = { item ->
                if (currUser != null) {
                    if (currUser!!.accountType == DataConstants.AccountType.admin) {
                        ItemEndContent(item.id, item.accepted, scheduleViewModel)
                    }
                }
            },
            onItemClick = {},
            pictureProvider = { item ->
                val vol = allVolunteers.firstOrNull() { it.id == item.userID }
                vol?.profilePic ?: R.drawable.product_image_not_found
            })
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
            if (currUser != null) {
                AddScheduleDialog(
                    onDissmiss = {
                        showDialog = false
                    },
                    volunteersList = allVolunteers,
                    selectedDate = selectedDate,
                    selectedInTime = selectedInTime,
                    selectedOutTime = selectedOutTime,
                    selectedVol = selectedVol,
                    selectedVolId = selectedVolId,
                    expandedVol = expandedVol,
                    usertype = currUser!!.accountType,
                    storesList = allStores,
                    selectStore = selectedStore,
                    selectStoreId = selectedStoreId,
                    expandedStore = expandedStore,
                    selectedWorkFunction = selectedWorkFunction
                ) {
                    val dateConvToDate = convertStringToDate(selectedDate.value)
                    if (currUser!!.accountType != DataConstants.AccountType.admin) {
                        selectedVolId.value = currUser!!.id
                    }
                    if (dateConvToDate != null) {
                        scheduleViewModel.insertNewSchedule(
                            VolunteerSchedule(
                                id = "",
                                accepted = false,
                                day = dateConvToDate,
                                startTime = selectedInTime.value,
                                endTime = selectedOutTime.value,
                                localId = selectedStoreId.value,
                                userID = selectedVolId.value,
                                workFunction = selectedWorkFunction.value
                            ), context
                        )
                    }
                    showDialog = false
                }
            } else {
                Toast.makeText(
                    context,
                    stringResource(R.string.error_creating_schedule),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun ItemEndContent(scheduleId: String, scheduleState: Boolean, viewModel: ScheduleViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var workfunctionSelected by remember { mutableStateOf(false) }
    var selectedWorkFunction = remember { mutableStateOf("") }
    var context = LocalContext.current

    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (!scheduleState) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.aprove))
                    },
                    onClick = {
                        expanded = false
                        viewModel.editSchedule(scheduleId, false, context)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.decline))
                    },
                    onClick = {
                        expanded = false
                        viewModel.editSchedule(scheduleId, true, context)
                    }
                )
            } else {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.function))
                    },
                    onClick = {
                        expanded = false
                        workfunctionSelected = true
                    }
                )
            }

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.delete))
                },
                onClick = {
                    expanded = false
                    viewModel.editSchedule(scheduleId, true, context)
                }
            )
        }

        if (workfunctionSelected) {
            WorkFunctionDialog(
                onDissmiss = { workfunctionSelected = false },
                onClick = {
                    viewModel.editSchedule(
                        scheduleId,
                        false,
                        context,
                        selectedWorkFunction.value
                    )
                }, selectedWorkFunction
            )
        }
    }
}