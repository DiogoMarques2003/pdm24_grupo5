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
import kotlinx.coroutines.launch

class LoginViewModel(context: Context, navController: NavController) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())
    private val _navController = navController

    fun login(email: String, password: String, context: Context) {
        if (email.isEmpty() || !EMAIL_ADDRESS.matcher(email).matches()) {
            return Toast.makeText(
                context,
                context.getString(R.string.invalid_email),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (password.isEmpty()) {
            return Toast.makeText(
                context,
                context.getString(R.string.invalid_password),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModelScope.launch {
            // Fazer login
            var userId: String? = FirebaseObj.loginAccount(email, password)
                ?: return@launch Toast.makeText(
                    context,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()

            // Obter o utilizador da firebase
            var firebaseUser = FirebaseObj.getData(DataConstants.FirebaseCollections.users, userId)
                ?: return@launch Toast.makeText(
                    context,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()

            // Converter o user da firebase para a class
            val usersConverted = firebaseUser.map { Users.firebaseMapToClass(it) }
            val user = usersConverted.first()

            // Inserir na base de dados local
            usersRepository.insert(user)

            Toast.makeText(
                context,
                context.getString(R.string.login_success),
                Toast.LENGTH_LONG,
            ).show()
            
            if (user.active) {
                // TODO: redirect to dashboard
            } else {
                // TODO: redirect to wait page
            }
        }
    }
}