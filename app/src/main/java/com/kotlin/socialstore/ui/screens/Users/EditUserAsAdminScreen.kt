package com.kotlin.socialstore.ui.screens.Users


import WarningDropdown
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.ProfileViewModel

@Composable
fun EditUserAsAdminScreen(
    navController: NavController,
    modifier: Modifier,
    profileViewModel: ProfileViewModel
) {
    val userInfo by profileViewModel.userToEditAsAdmin.collectAsState(null)
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
    val phoneCountryCode = remember { mutableStateOf(userInfo?.phoneCountryCode ?: "") }
    val phoneNumber = remember { mutableStateOf(userInfo?.phoneNumber ?: "") }
    val nationality = remember { mutableStateOf(userInfo?.nationality ?: "") }
    var notas = remember { mutableStateOf(userInfo?.notes ?: "") }
    var selectedWarningText = remember { mutableStateOf(userInfo?.warningsLevel ?: "") }



    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            name.value = user.name
            email.value = user.email
            phoneNumber.value = user.phoneNumber
            phoneCountryCode.value = user.phoneCountryCode
            nationality.value = user.nationality ?: ""
            notas.value = user.notes ?: ""
            selectedWarningText.value = user.warningsLevel ?: ""
        }
    }

    BackgroundImageElement()

    if (userInfo == null) {
        //CircularProgressIndicator(modifier = Modifier.fillMaxSize())
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

                // User image
                Row(modifier = Modifier) {
                    SubcomposeAsyncImage(
                        model = userInfo?.profilePic ?: R.drawable.profile_image_not_found,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // First name of user - Title
                Text(
                    text = name.value.split(" ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {


                    // Full Name
                    OutlinedTextfieldElement(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        onValueChange = { },
                        value = name.value,
                        labelText = stringResource(R.string.full_name_text_field)
                    )

                    // Nacionalidade
                    OutlinedTextfieldElement(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        onValueChange = { },
                        value = nationality.value,
                        labelText = stringResource(R.string.nationality_text_field)
                    )

                    // Email
                    OutlinedTextfieldElement(
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { },
                        value = email.value,
                        enabled = false,
                        readOnly = true,
                        labelText = stringResource(R.string.email_textfield),
                        trailingIcon = {}
                    )

                    OutlinedTextfieldElement(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        onValueChange = { },
                        value = phoneCountryCode.value + " " + phoneNumber.value,
                        labelText = stringResource(R.string.phoneNumber_textfield)
                    )

                    OutlinedTextfieldElement(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onValueChange = { notas.value = it },
                        value = notas.value,
                        singleLine = false,
                        labelText = stringResource(R.string.notes_textfield)
                    )

                    WarningDropdown(
                        selectedColor = selectedWarningText,
                        onWarningChanged = { warning ->
                            selectedWarningText.value = warning
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        profileViewModel.updateUserInfoAsAdmin(
                            userInfo?.id,
                            notas.value,
                            selectedWarningText.value,
                            context,
                            navController
                        )
                    },
                    enabled = userInfo?.notes != notas.value || userInfo?.warningsLevel != selectedWarningText.value,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save Changes")
                }
            }
        }
    }
}

