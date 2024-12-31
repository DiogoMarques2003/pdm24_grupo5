package com.kotlin.socialstore.ui.screens


import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.viewModels.ProfileViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    modifier: Modifier,
    profileViewModel: ProfileViewModel
) {
    val userInfo by profileViewModel.userData.collectAsState(null)
    val context = LocalContext.current

    val name = remember { mutableStateOf(userInfo?.name ?: "") }
    val email = remember { mutableStateOf(userInfo?.email ?: "") }
    val password = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf(userInfo?.phoneNumber ?: "") }
    val nationality = remember { mutableStateOf(userInfo?.nationality ?: "") }
    var isEmailValid by remember { mutableStateOf(true) }


    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            name.value = user.name
            email.value = user.email ?: ""
            phoneNumber.value = user.phoneNumber ?: ""
            nationality.value = user.nationality ?: ""
        }
    }

    if (userInfo == null) {
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextfieldElement(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    email.value = it
                        EMAIL_ADDRESS.matcher(it).matches() // Check if is a valid email
                },
                value = email.value,
                labelText = stringResource(R.string.email_textfield),
                leadingIcon = Icons.Default.Person,
                trailingIcon = {},
                isError = if (email.value == "") false else !isEmailValid
            )

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nationality.value,
                onValueChange = { nationality.value = it },
                label = { Text("Nationality") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    profileViewModel.updateUserInfo(
                        name = name.value,
                        email = email.value,
                        password = password.value,
                        phoneNumber = phoneNumber.value,
                        nationality = nationality.value,
                        context = context
                    )

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Changes")
            }

        }
    }
}

