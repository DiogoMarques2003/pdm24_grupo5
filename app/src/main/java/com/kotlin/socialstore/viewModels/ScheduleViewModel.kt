package com.kotlin.socialstore.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.VolunteerScheduleRepository
import kotlinx.coroutines.launch

class ScheduleViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private var scheduleListener: ListenerRegistration? = null
    private val scheduleRepository = VolunteerScheduleRepository(database.volunteerScheduleDao())

    val allSchedules = scheduleRepository.allVolunteerSchedule

    fun getSchedules(context: Context){
        viewModelScope.launch {
            scheduleListener = FirebaseObj.listenToData(
                DataConstants.FirebaseCollections.volunteerSchedule,
                null,
                { },//updateVolunteerSchedule(it) },
                { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
        }
    }

    private fun updateVolunteerSchedule(volSchedule: List<Map<String, Any>>?){
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
}