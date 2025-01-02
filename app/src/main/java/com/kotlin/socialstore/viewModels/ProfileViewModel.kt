package com.kotlin.socialstore.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebaseEmail
import com.kotlin.socialstore.data.firebase.FirebaseObj.updateFirebasePassword
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var userListener: ListenerRegistration? = null
    private val userRepository = UsersRepository(database.usersDao())
    private val currUser = FirebaseObj.getCurrentUser()

    val userData = userRepository.getById(currUser!!.uid)

    fun getUserInfo(context: Context) {
        //Add Listener to User
        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            currUser?.uid,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?) {
        viewModelScope.launch {
            //Get user
            val user = users?.firstOrNull() ?: return@launch

            //Convert firebase data to local db data
            val userConv = Users.firebaseMapToClass(user)

            //Delete all local data
            userRepository.deleteById(userConv.id)

            //Get Image
            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            //Insert new data
            userRepository.insert(userConv)
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
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update(updatedData)
                    .await()

                // Atualizar dados no Firebase Authentication
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

            val userFirebase = FirebaseObj.getCurrentUser() ?: return@launch

            userRepository.deleteById(userFirebase.uid)

            FirebaseObj.logoutAccount()

            // Navigate to the login screen
            navController.navigate("login_screen") {
                popUpTo(0) { inclusive = true } // Clear navigation history
            }
        }
    }
    fun uploadProfileImage(uri: Uri, context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("/profileImages/${currentUser.uid}.jpg")

        viewModelScope.launch {
            try {
                storageRef.putFile(uri).await()

                val imagePath = "profileImages/${currentUser.uid}.jpg"

                val userDocRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)


                userDocRef.update("profilePic", imagePath).await()

                Toast.makeText(context, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ProfilePicture", "Error uploading profile picture: ${e.message}", e)
                Toast.makeText(context, "Failed to update profile picture.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}




