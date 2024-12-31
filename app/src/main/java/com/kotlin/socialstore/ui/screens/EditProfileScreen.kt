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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.NationalityDropdown
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PasswordTextField
import com.kotlin.socialstore.viewModels.ProfileViewModel
import com.togitech.ccp.component.TogiCountryCodePicker

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
    var phoneCountryCode by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }


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
                onValueChange = { name.value = it },
                value = name.value,
                labelText = stringResource(R.string.full_name_text_field)
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

            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { password.value = it },
                value = password.value,
                labelText = stringResource(R.string.password_textfield)
            )

            TogiCountryCodePicker(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { (code, phone), valid ->
                    phoneNumber.value = phone
                    phoneCountryCode = code
                    isPhoneNumberValid = valid
                },
                label = { Text(stringResource(R.string.phoneNumber_textfield)) },
                showError = phoneNumber.value != "",
                clearIcon = null,
                initialCountryIsoCode = "PT",
                shape = UiConstants.outlinedTextFieldElementShape,
            )


            NationalityDropdown(selectedNationality = nationality.value,
                onNationalitySelected = { nationality.value = it })

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

