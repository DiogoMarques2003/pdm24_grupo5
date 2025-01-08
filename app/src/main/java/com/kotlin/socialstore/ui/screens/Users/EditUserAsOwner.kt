package com.kotlin.socialstore.ui.screens.Users


import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebaseEmail
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebasePassword
import com.kotlin.socialstore.ui.elements.dropDown.NationalityDropdown
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PasswordTextField
import com.kotlin.socialstore.ui.elements.PopBackButton
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
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { selectedUri ->
                profileViewModel.uploadProfileImage(
                    uri = selectedUri,
                    context = context
                )
            }
        }
    )


    val name = remember { mutableStateOf(userInfo?.name ?: "") }
    val email = remember { mutableStateOf(userInfo?.email ?: "") }
    val password = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf(userInfo?.phoneNumber ?: "") }
    val nationality = remember { mutableStateOf(userInfo?.nationality ?: "") }
    var isEmailValid by remember { mutableStateOf(true) }
    var phoneCountryCode by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var credencialchanged by remember { mutableStateOf(false) }


    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            name.value = user.name
            email.value = user.email ?: ""
            phoneNumber.value = user.phoneNumber ?: ""
            nationality.value = user.nationality ?: ""
        }
    }

    if (userInfo == null) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PopBackButton(navController)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier) {
                    SubcomposeAsyncImage(
                        model = userInfo?.profilePic ?: R.drawable.product_image_not_found,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .height(50.dp)
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Change Picture",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }


                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        if (email.value.isNotBlank() && email.value != userInfo?.email) {
                            updateFirebaseEmail(email.value) { success, message ->
                                if (success) {
                                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                    Toast.makeText(
                                        context,
                                        "Please verify your email. Changes will apply after verification.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to update email: $message",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        if (password.value.isNotBlank()) {
                            updateFirebasePassword(password.value) { success, message ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Password updated successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to update password: $message",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        profileViewModel.updateUserInfo(
                            name = name.value,
                            email = email.value,
                            password = password.value,
                            phoneNumber = phoneNumber.value,
                            nationality = nationality.value,
                            context = context
                        )

                        if (credencialchanged) {
                            FirebaseAuth.getInstance().signOut()
                            profileViewModel.logoutUser(navController)
                        }

                        navController.navigate("login_screen") {
                            popUpTo(0) { inclusive = true }
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save Changes")
                }
            }
        }
    }
}

