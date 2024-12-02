package com.kotlin.socialstore.ui.screens

import android.provider.Settings.Global.getString
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PasswordTextField
import com.kotlin.socialstore.ui.elements.buttonElement
import com.kotlin.socialstore.ui.theme.SocialStoreTheme

//DEFAULT VALUES
val defaultPadding = 16.dp
val itemSpacing = 8.dp
val imageContentSpacing = 40.dp

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginPage(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberCred by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(defaultPadding),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(R.drawable.social_store_image_no_background),
                contentDescription = "Image",
                modifier = Modifier.size(250.dp)
            )
        }
        Spacer(Modifier.height(imageContentSpacing))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.app_name))
            Spacer(Modifier.height(itemSpacing))
            OutlinedTextfieldElement(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { email = it },
                value = email,
                labelText = stringResource(R.string.email_textfield),
                leadingIcon = Icons.Default.Person,
                trailingIcon = {}
            )
            /*
            OutlinedTextfieldElement(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { password = it },
                value = password,
                labelText = stringResource(R.string.password_textfield),
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {},
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )*/
            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { password = it },
                value = password,
                labelText = stringResource(R.string.password_textfield)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Remember me
                    Checkbox(
                        checked = rememberCred,
                        onCheckedChange = { rememberCred = !rememberCred })
                    Text(stringResource(R.string.login_remember_me))
                }
                Row {
                    // Forgot Password
                    TextButton(onClick = {}) {
                        Text(stringResource(R.string.login_forgot_password))
                    }
                }
            }
            Spacer(Modifier.height(itemSpacing))
            Button(
                onClick = {}, //Submits login
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login_button))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.login_create_account_text))
                TextButton(onClick = {}){
                    Text(stringResource(R.string.login_create_account_button))
                }
            }
        }
    }

}