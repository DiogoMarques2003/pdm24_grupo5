package com.kotlin.socialstore.ui.elements.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.ButtonElement

@Composable
fun ConfirmPopup(
    title: String,
    text: String,
    confirmButton: () -> Unit,
    dismissButton: () -> Unit,
    showPopup: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { showPopup.value = false },
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            ButtonElement(
                text = stringResource(R.string.yes),
                modifier = Modifier.fillMaxWidth(),
                onClick = confirmButton
            )
        },
        dismissButton = {
            ButtonElement(
                text = stringResource(R.string.no),
                modifier = Modifier.fillMaxWidth(),
                onClick = dismissButton
            )
        }
    )
}