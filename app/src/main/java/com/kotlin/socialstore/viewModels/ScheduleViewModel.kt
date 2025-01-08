package com.kotlin.socialstore.viewModels

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.VolunteerSchedule
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.StoresRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import com.kotlin.socialstore.data.repository.VolunteerScheduleRepository
import compareTimes
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
            val volunteersConv = volunteers.map {
                val vol = Users.firebaseMapToClass(it)
                if (vol.profilePic != null) {
                    vol.profilePic = FirebaseObj.getImageUrl(vol.profilePic!!)
                }
                vol
            }

            //Delete volunteers local data
            usersRepository.deleteByTypeAccount(DataConstants.AccountType.volunteer)

            //Insert new data
            usersRepository.insertList(volunteersConv)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNewSchedule(data: VolunteerSchedule, context: Context) {
        viewModelScope.launch {
            if ( data.endTime.isEmpty() || data.startTime.isEmpty() || data.localId.isNullOrEmpty() ){
                Toast.makeText(context, context.getString(R.string.fill_fields), Toast.LENGTH_SHORT).show()
                return@launch
            }
            val compareTimes = compareTimes(data.startTime,data.endTime)
            if (compareTimes == 0 || compareTimes == 1){
                Toast.makeText(context, context.getString(R.string.valid_hours), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val volConv = data.toFirebaseMap()
            val result =
                FirebaseObj.insertData(DataConstants.FirebaseCollections.volunteerSchedule, volConv)
            if (result.isNullOrEmpty()) {
                Toast.makeText(context, context.getString(R.string.error_insert_schedule), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun editSchedule(
        scheduleId: String,
        delete: Boolean = false,
        context: Context,
        workFunction: String? = null
    ) {
        viewModelScope.launch {
            if (delete  && workFunction.isNullOrEmpty()) {
                val res = FirebaseObj.deleteData(
                    DataConstants.FirebaseCollections.volunteerSchedule,
                    scheduleId
                )
                if (res) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.schdeule_removed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_remove_schedule),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if(!delete  && workFunction.isNullOrEmpty()) {
                val freshVolSchedData = FirebaseObj.getData(
                    DataConstants.FirebaseCollections.volunteerSchedule,
                    scheduleId
                )
                if (!freshVolSchedData.isNullOrEmpty()) {
                    val firstVol = freshVolSchedData.firstOrNull()
                    if (firstVol.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_update_schedule),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    var dataConv = VolunteerSchedule.firebaseMapToClass(firstVol)
                    dataConv.accepted = !dataConv.accepted
                    val res = FirebaseObj.updateData(
                        DataConstants.FirebaseCollections.volunteerSchedule,
                        scheduleId,
                        dataConv.toFirebaseMap()
                    )
                    if (res) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.schedule_update),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_update_schedule),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_update_schedule),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (!workFunction.isNullOrEmpty()) {
                val freshSchedData = FirebaseObj.getData(
                    DataConstants.FirebaseCollections.volunteerSchedule,
                    scheduleId
                )
                if (!freshSchedData.isNullOrEmpty()) {
                    val firstVol = freshSchedData.firstOrNull()
                    if (firstVol.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_update_schedule),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    var dataConvWork = VolunteerSchedule.firebaseMapToClass(firstVol)
                    dataConvWork.workFunction = workFunction
                    val res = FirebaseObj.updateData(
                        DataConstants.FirebaseCollections.volunteerSchedule,
                        scheduleId,
                        dataConvWork.toFirebaseMap()
                    )
                    if (res) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.schedule_update),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_update_schedule),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}