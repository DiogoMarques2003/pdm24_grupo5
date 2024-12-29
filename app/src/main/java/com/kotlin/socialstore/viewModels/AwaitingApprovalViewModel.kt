package com.kotlin.socialstore.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.launch

class AwaitingApprovalViewModel(context: Context, val navController: NavController) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())

    fun logoutUser() {
        viewModelScope.launch {
            // Check if user is logged or not
            val userFirebase = FirebaseObj.getCurrentUser() ?: return@launch

            // Delete user from local database
            usersRepository.deleteById(userFirebase.uid)
            // Logout user from firebase
            FirebaseObj.logoutAccount()

            // Send to home age
            navController.navigate("home_screen") {
                popUpTo(0) { inclusive = true } // clear navigation history
            }
        }
    }
}