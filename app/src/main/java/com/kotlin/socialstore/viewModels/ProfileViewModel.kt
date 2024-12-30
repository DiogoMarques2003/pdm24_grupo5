package com.kotlin.socialstore.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())

    var userInfo = MutableStateFlow<Users?>(null)

    fun loadInfo() {
        viewModelScope.launch {
            val userFirebase = FirebaseObj.getCurrentUser()
            userInfo.value = usersRepository.getById(userFirebase!!.uid).first()
            if (userInfo.value?.profilePic != null){
                userInfo.value!!.profilePic =  FirebaseObj.getImageUrl(userInfo.value!!.profilePic!!)
            }
        }
    }
}