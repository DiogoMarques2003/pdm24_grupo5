package com.kotlin.socialstore.ui.elements

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerElement(selectedDate: MutableState<String>) {
    val focusManager = LocalFocusManager.current
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePickerDialog) {
        DatePickerDialog(onDismissRequest = { showDatePickerDialog = false }, confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedDate.value = millis.toDateFormat()
                }
                showDatePickerDialog = false
            }) {
                Text(stringResource(R.string.choose_date))
            }
        }) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    OutlinedTextfieldElement(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .onFocusEvent {
                if (it.isFocused) {
                    showDatePickerDialog = true
                    focusManager.clearFocus(force = true)
                }
            },
        onValueChange = { },
        value = selectedDate.value,
        labelText = stringResource(R.string.date),
        leadingIcon = Icons.Filled.CalendarMonth,
        trailingIcon = {}
    )
}

fun Long.toDateFormat(
    pattern: String = "dd/MM/yyyy"
): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(
        pattern, Locale(Locale.getDefault().toString())
    ).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return formatter.format(date)
}