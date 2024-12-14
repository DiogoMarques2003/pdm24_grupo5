package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PasswordTextField
import com.kotlin.socialstore.ui.elements.RadioButtonElement
import com.togitech.ccp.component.TogiCountryCodePicker
import android.util.Patterns.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalContext
import com.kotlin.socialstore.viewModels.RegisterViewModel

@Composable
fun RegisterPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel
) {
    var isBeneficiary by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCountryCode by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Add backgroud image
    BackgroundImageElement()

    Column ( modifier = modifier.fillMaxSize(),
             verticalArrangement = Arrangement.Top) {

        // Beneficiary or Volunteer
        Row (Modifier.fillMaxWidth()
            .padding(bottom = 16.dp),
             horizontalArrangement = Arrangement.SpaceEvenly,
             verticalAlignment = Alignment.CenterVertically) {
            // Beneficiary Radio Button with label
            RadioButtonElement(stringResource(R.string.beneficiary), isBeneficiary) { isBeneficiary = true }

            // Volunteer Radio Button with label
            RadioButtonElement(stringResource(R.string.volunteer), !isBeneficiary) { isBeneficiary = false }
        }

        Spacer(Modifier.height(UiConstants.itemSpacing))

        // Full name input
        OutlinedTextfieldElement(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { fullName = it },
            value = fullName,
            labelText = stringResource(R.string.full_name_text_field)
        )

        Spacer(Modifier.height(UiConstants.itemSpacing))
        NationalityDropdown(
            selectedNationality = nationality,
            onNationalitySelected = { nationality = it }
        )

        Spacer(Modifier.height(UiConstants.itemSpacing))

        // Phone number/Phone Country code
        TogiCountryCodePicker(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { (code, phone), valid ->
                phoneNumber = phone
                phoneCountryCode = code
                isPhoneNumberValid = valid
            },
            label = { Text(stringResource(R.string.phoneNumber_textfield)) },
            showError = phoneNumber != "",
            clearIcon = null,
            initialCountryIsoCode = "PT",
            shape = UiConstants.outlinedTextFieldElementShape,
        )

        Spacer(Modifier.height(UiConstants.itemSpacing))

        // Email
        OutlinedTextfieldElement(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                email = it
                isEmailValid = EMAIL_ADDRESS.matcher(it).matches() // Check if is a valid email
            },
            value = email,
            labelText = stringResource(R.string.email_textfield),
            isError = if (email == "") false else !isEmailValid
        )

        Spacer(Modifier.height(UiConstants.itemSpacing))

        // Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { password = it },
            value = password,
            labelText = stringResource(R.string.password_textfield)
        )

        Spacer(Modifier.height(UiConstants.itemSpacing))

        // Confirm Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { confirmPassword = it },
            value = confirmPassword,
            labelText = stringResource(R.string.confirmpassword_textfield)
        )

        if (isBeneficiary) {
            Spacer(Modifier.height(UiConstants.itemSpacing))
            // Reference
            OutlinedTextfieldElement(
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { reference = it },
                value = reference,
                labelText = stringResource(R.string.reference_textfield)
            )
        }
    }

    // Register button in the end of the page
    Box(modifier = modifier.fillMaxSize()) {
        ButtonElement(onClick = {
            //Firebase
            //FirebaseObj.createAccount(email,password,context)
            registerViewModel.register(email,  password, confirmPassword, isBeneficiary,
                                       fullName, nationality, phoneNumber, phoneCountryCode,
                                       isPhoneNumberValid, reference, context)
        },
            text = stringResource(R.string.login_create_account_button),
            modifier = Modifier.fillMaxWidth()
                               .align(Alignment.BottomEnd))
    }
}