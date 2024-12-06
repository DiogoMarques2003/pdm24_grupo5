package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PasswordTextField
import com.kotlin.socialstore.ui.elements.PopBackButton

//DEFAULT VALUES
private val defaultPadding = 16.dp
private val itemSpacing = 8.dp
private val imageContentSpacing = 40.dp

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberCred by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                PopBackButton(navController)
            }
            Image(
                painter = painterResource(R.drawable.social_store_image_no_background),
                contentDescription = "Image",
                modifier = Modifier.size(270.dp)
            )
        }
        Spacer(Modifier.height(imageContentSpacing))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(itemSpacing))
            OutlinedTextfieldElement(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { email = it },
                value = email,
                labelText = stringResource(R.string.email_textfield),
                leadingIcon = Icons.Default.Person,
                trailingIcon = {}
            )
            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { password = it },
                value = password,
                labelText = stringResource(R.string.password_textfield)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Remember me
                    Checkbox(
                        checked = rememberCred,
                        onCheckedChange = { rememberCred = !rememberCred })
                    Text(stringResource(R.string.login_remember_me))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Forgot Password
                    Text(
                        modifier = Modifier.clickable { },
                        text = stringResource(R.string.login_forgot_password),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(itemSpacing))
            ButtonElement(
                onClick = { },
                text = stringResource(R.string.login_button),
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.login_create_account_text))
                Spacer(Modifier.height(itemSpacing))
                Text(
                    modifier = Modifier.clickable { navController.navigate("register_screen") },
                    text = stringResource(R.string.login_create_account_button),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

}