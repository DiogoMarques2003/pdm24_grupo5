package com.kotlin.socialstore.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var userListener : ListenerRegistration? = null
    private val userRepository = UsersRepository(database.usersDao())
    private val currUser = FirebaseObj.getCurrentUser()

    val userData = userRepository.getById(currUser!!.uid)

    fun getUserInfo(context: Context){
        //Add Listener to User
        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            currUser?.uid,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?){
        viewModelScope.launch {
            //Get user
            val user = users?.firstOrNull() ?: return@launch

            //Convert firebase data to local db data
            val userConv = Users.firebaseMapToClass(user)

            //Delete all local data
            userRepository.deleteById(userConv.id)

            //Get Image
            if ( userConv.profilePic != null ){
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            //Insert new data
            userRepository.insert(userConv)
        }
    }
}