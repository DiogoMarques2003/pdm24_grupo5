package com.kotlin.socialstore.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.TakenItemsCategoryCount
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebaseEmail
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebasePassword
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.TakenItemsRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var userListener: ListenerRegistration? = null
    private var takenItemsListener: ListenerRegistration? = null
    private val userRepository = UsersRepository(database.usersDao())
    private val takenItemsRepository = TakenItemsRepository(database.takenItemsDao())
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val currUser = FirebaseAuth.getInstance().currentUser

    val userData = userRepository.getById(currUser!!.uid)
    var takenItems = takenItemsRepository.getTakenItemsByHousehold(currUser!!.uid)
    val categories = categoryRepository.allCategories

    fun getUserInfo(context: Context) {
        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            currUser?.uid,
            { updateUserInfo(it) },
            {
                Toast.makeText(context, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT)
                    .show()
            })

        takenItemsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.takenItems,
            null,
            { updateTakenItems(it) },
            { Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show() }
        )

        viewModelScope.launch {
            val categories =
                FirebaseObj.getData(DataConstants.FirebaseCollections.category) ?: return@launch
            val categoriesClass = categories.map { Category.firebaseMapToClass(it) }
            categoryRepository.deleteAll()
            categoryRepository.insertList(categoriesClass)
        }
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?) {
        viewModelScope.launch {
            val user = users?.firstOrNull() ?: return@launch
            val userConv = Users.firebaseMapToClass(user)

            userRepository.deleteById(userConv.id)

            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            userRepository.insert(userConv)
        }
    }

    private fun updateTakenItems(takenItemsList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (takenItemsList == null) {
                return@launch takenItemsRepository.deleteAll()
            }

            val takenItemsClass = takenItemsList.map { TakenItems.firebaseMapToClass(it) }

            takenItemsRepository.deleteAll()
            takenItemsRepository.insertList(takenItemsClass)
        }
    }

    fun updateUserInfo(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        nationality: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val updatedData = mapOf(
                    "name" to name,
                    "email" to email,
                    "phoneNumber" to phoneNumber,
                    "nationality" to nationality
                )
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currUser!!.uid)
                    .update(updatedData)
                    .await()

                updateFirebaseEmail(email) { emailSuccess, emailError ->
                    if (!emailSuccess) {
                        Log.e("ProfileUpdate", "Failed to update email: $emailError")
                    }
                }

                updateFirebasePassword(password) { passwordSuccess, passwordError ->
                    if (!passwordSuccess) {
                        Log.e("ProfileUpdate", "Failed to update password: $passwordError")
                    }
                }

            } catch (e: Exception) {
                Log.e("ProfileUpdate", "Error updating profile: ${e.message}", e)
            }
        }
    }

    fun logoutUser(navController: NavController) {
        viewModelScope.launch {
            userRepository.deleteById(currUser!!.uid)
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val storageRef =
            FirebaseStorage.getInstance().reference.child("profileImages/${currentUser.uid}.jpg")

        viewModelScope.launch {
            try {
                storageRef.putFile(uri).await()

                val imagePath = "profileImages/${currentUser.uid}.jpg"

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .update("profilePic", imagePath)
                    .await()

                Toast.makeText(context, "Profile picture updated successfully.", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e("ProfilePicture", "Error uploading profile picture: ${e.message}", e)
                Toast.makeText(context, "Failed to update profile picture.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}







