package com.kotlin.socialstore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImage
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.RadioButton

@Composable
fun RegisterPage(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var isBeneficiary by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCountryCode by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }

    // Add the backgroud image
    BackgroundImage()

    Column ( modifier = modifier.fillMaxSize(),
             verticalArrangement = Arrangement.Top ) {

        // Beneficiary or Volunteer
        Row (Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.SpaceEvenly,
             verticalAlignment = Alignment.CenterVertically) {
            // Beneficiary Radio Button with label
            RadioButton(stringResource(R.string.beneficiary), isBeneficiary) { isBeneficiary = true }

            // Volunteer Radio Button with label
            RadioButton(stringResource(R.string.volunteer), !isBeneficiary) { isBeneficiary = false }
        }

        // Full name input
        OutlinedTextfieldElement(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { fullName = it },
            value = fullName,
            labelText = stringResource(R.string.full_name_text_field)
        )

        if (isBeneficiary) {
            // Nationality input
            OutlinedTextfieldElement(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { nationality = it },
                value = nationality,
                labelText = stringResource(R.string.nationality_text_field)
            )
        }

        // Phone number/Phone Country code
    }
}