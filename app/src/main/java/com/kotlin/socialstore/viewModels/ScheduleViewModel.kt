package com.kotlin.socialstore.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.StoresRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import com.kotlin.socialstore.data.repository.VolunteerScheduleRepository
import kotlinx.coroutines.launch

class ScheduleViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private var scheduleListener: ListenerRegistration? = null
    private val scheduleRepository = VolunteerScheduleRepository(database.volunteerScheduleDao())
    private val usersRepository = UsersRepository(database.usersDao())
    private val storesRepository = StoresRepository(database.storesDao())

    val allSchedules = scheduleRepository.allVolunteerSchedule
    val allVolunteers = usersRepository.getByTypeAccount(DataConstants.AccountType.volunteer)
    val allStores = storesRepository.allStores
    val currUser = usersRepository.getById(FirebaseObj.getCurrentUser()!!.uid)

    fun getSchedules(context: Context) {
        scheduleListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.volunteerSchedule,
            null,
            { updateVolunteerSchedule(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    private fun updateVolunteerSchedule(volSchedule: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (volSchedule == null) {
                //Delete all local data
                return@launch scheduleRepository.deleteAll()
            }

            //Convert firebase data to local db data
            val volScheduleConv = volSchedule.map { VolunteerSchedule.firebaseMapToClass(it) }

            //Delete all local data
            scheduleRepository.deleteAll()

            //Insert new data
            scheduleRepository.insertList(volScheduleConv)
        }
    }

    fun getVolunteers() {
        viewModelScope.launch {
            val volunteers = FirebaseObj.getData(
                DataConstants.FirebaseCollections.users,
                null,
                "accountType",
                DataConstants.AccountType.volunteer
            ) ?: return@launch

            //Convert volunteers
            val volunteersConv = volunteers.map { Users.firebaseMapToClass(it) }

            //Delete volunteers local data
            usersRepository.deleteByTypeAccount(DataConstants.AccountType.volunteer)

            //Insert new data
            usersRepository.insertList(volunteersConv)
        }
    }

    fun insertNewSchedule(data: VolunteerSchedule){
        viewModelScope.launch {
            /*
           var volSched = data
            volSched.
            val volConv = VolunteerSchedule.firebaseMapToClass(it)
            FirebaseObj.insertData(DataConstants.FirebaseCollections.volunteerSchedule,volConv)*/
        }
    }
}