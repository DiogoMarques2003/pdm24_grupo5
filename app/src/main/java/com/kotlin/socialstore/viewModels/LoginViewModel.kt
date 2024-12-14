package com.kotlin.socialstore.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository

class LoginViewModel(context: Context): ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())

    fun login(email: String, password: String, context: Context) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseObj.loginAccount(email, password, context)

            // TODO: If the account is logged get the user from firebase and insert on local database
        } else {
            //error display
        }
    }
}