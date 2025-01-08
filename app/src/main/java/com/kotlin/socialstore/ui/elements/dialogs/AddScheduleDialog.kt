package com.kotlin.socialstore.ui.elements.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.ui.elements.DatePickerElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.TimePickerWithDialog
import com.kotlin.socialstore.ui.elements.dropDown.DropDownBox

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDissmiss: () -> Unit, volunteersList: List<Users>,
    storesList: List<Stores> = emptyList(),
    selectStore: MutableState<String>,
    selectStoreId: MutableState<String>,
    selectedDate: MutableState<String>,
    selectedInTime: MutableState<String>,
    selectedOutTime: MutableState<String>,
    selectedVol: MutableState<String>,
    selectedVolId: MutableState<String>,
    selectedWorkFunction: MutableState<String>,
    expandedVol: MutableState<Boolean>,
    expandedStore: MutableState<Boolean>,
    usertype: String,
    onClick: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDissmiss,
        modifier = Modifier.fillMaxWidth(0.92f),
        properties = DialogProperties(dismissOnClickOutside = true),
        content = {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.create_new_schedule), fontSize = 20.sp)
                    if (usertype == DataConstants.AccountType.admin) {
                        DropDownBox(expandedVol, selectedVol, stringResource(R.string.volunteer)) {
                            volunteersList.forEach { vol ->
                                DropdownMenuItem(
                                    text = { Text(vol.name) },
                                    onClick = {
                                        expandedVol.value = false
                                        selectedVol.value = vol.name
                                        selectedVolId.value = vol.id
                                    }
                                )
                            }
                        }

                        OutlinedTextfieldElement(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onValueChange = {
                                selectedWorkFunction.value = it
                            },
                            value = selectedWorkFunction.value,
                            labelText = stringResource(R.string.work_function),
                            leadingIcon = Icons.Default.Desk,
                            trailingIcon = {}
                        )
                    }
                    DatePickerElement(selectedDate)
                    TimePickerWithDialog(selectedInTime, stringResource(R.string.start_time))
                    TimePickerWithDialog(selectedOutTime, stringResource(R.string.end_time))
                    if (storesList.isNotEmpty()) {
                        DropDownBox(
                            expandedStore,
                            selectStore,
                            stringResource(R.string.store_Sched)
                        ) {
                            storesList.forEach { store ->
                                DropdownMenuItem(
                                    text = { Text(store.name) },
                                    onClick = {
                                        expandedStore.value = false
                                        selectStore.value = store.name
                                        selectStoreId.value = store.id
                                    }
                                )
                            }
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDissmiss() }) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            onClick()
                        }) {
                            Text(stringResource(R.string.confirm))
                        }
                    }

                }
            }
        }
    )
}
