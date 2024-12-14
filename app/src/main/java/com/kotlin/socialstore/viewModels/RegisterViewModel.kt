package com.kotlin.socialstore.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository

class RegisterViewModel(context: Context): ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())

    fun register(email: String, password: String, confirmPassword: String, isBeneficiary: Boolean,
                 fullName: String, nationality: String, phoneNumber: String, phoneCountryCode: String,
                 isPhoneNumberValid: Boolean, reference: String, context: Context) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
            && password == confirmPassword) {
            FirebaseObj.createAccount(email,password,context)

            // TODO: If the account is created add the user to firebase and insert on local database
        } else {
            //error display
        }
    }
}