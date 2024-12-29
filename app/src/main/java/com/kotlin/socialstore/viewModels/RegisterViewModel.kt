package com.kotlin.socialstore.viewModels

import android.content.Context
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(context: Context, val navController: NavController) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())

    var isProcessingRequest = MutableStateFlow(false)

    fun register(
        email: String, password: String, confirmPassword: String, isBeneficiary: Boolean,
        fullName: String, nationality: String, phoneNumber: String, phoneCountryCode: String,
        isPhoneNumberValid: Boolean, reference: String, context: Context
    ) {
        if (email.isEmpty() || !EMAIL_ADDRESS.matcher(email).matches()) {
            return Toast.makeText(
                context,
                context.getString(R.string.invalid_email),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (password.isEmpty() || password.isEmpty() || password != confirmPassword) {
            return Toast.makeText(
                context,
                context.getString(R.string.invalid_password),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (!isPhoneNumberValid) {
            return Toast.makeText(
                context,
                context.getString(R.string.invalid_phone_number),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModelScope.launch {
            try {
                // Set processing to true
                isProcessingRequest.value = true
                val userId = FirebaseObj.createAccount(email, password)
                    ?: return@launch Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                // criar objeto do user
                val user = Users(
                    id = userId,
                    email = email,
                    accountType = if (isBeneficiary) DataConstants.AccountType.benefiaryy else DataConstants.AccountType.volunteer,
                    active = false,
                    name = fullName,
                    phoneNumber = phoneNumber,
                    phoneCountryCode = phoneCountryCode,
                    profilePic = null,
                    nationality = nationality,
                    reference = reference,
                    notes = null,
                    warningsLevel = null,
                    familyHouseholdID = null,
                    familyHouseholdVerified = false
                )

                // Inserir na firebase
                FirebaseObj.insertData(
                    DataConstants.FirebaseCollections.users,
                    user.toFirebaseMap(),
                    userId
                )
                    ?: return@launch Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                // inserir na base de dados local
                usersRepository.insert(user)

                navController.navigate("awaiting_approval_screen")
            } finally {
                // Set processing to false
                isProcessingRequest.value = false
            }
        }
    }
}