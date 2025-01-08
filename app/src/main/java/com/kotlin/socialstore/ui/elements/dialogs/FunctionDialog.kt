package com.kotlin.socialstore.ui.elements.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
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
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkFunctionDialog(
    onDissmiss: () -> Unit,
    onClick: () -> Unit,
    selectedWorkFunction: MutableState<String>
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
                    Text(stringResource(R.string.choose_work_function), fontSize = 20.sp)

                    OutlinedTextfieldElement(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = {
                            selectedWorkFunction.value = it
                        },
                        value = selectedWorkFunction.value,
                        labelText = stringResource(R.string.work_function),
                        leadingIcon = Icons.Default.Desk,
                        trailingIcon = {}
                    )

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