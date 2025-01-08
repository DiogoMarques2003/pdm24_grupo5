package com.kotlin.socialstore.ui.elements.dialogs

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@Composable
fun DialogElement(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = content
    )
}

@Composable
fun DialogForgotPassword(baseContext: Context) {
    var email by remember { mutableStateOf("") }

    DialogElement( onDismissRequest = {}) {
        OutlinedTextfieldElement(
            modifier = Modifier,
            onValueChange = { email = it },
            labelText = "email",
            value = "email"
        )
        FirebaseObj.sendPasswordResetEmail(email,baseContext)
    }
}