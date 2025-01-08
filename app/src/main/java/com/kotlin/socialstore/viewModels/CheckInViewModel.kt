package com.kotlin.socialstore.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.entity.UsersNotes
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.FamilyHouseholdVisitsRepository
import com.kotlin.socialstore.data.repository.TakenItemsRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class CheckInViewModel(context: Context, val userId: String) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val usersRepository = UsersRepository(database.usersDao())
    private val householdVisitsRepository =
        FamilyHouseholdVisitsRepository(database.familyHouseholdVisitsDao())
    private val householdTakenItemsRepository = TakenItemsRepository(database.takenItemsDao())

    val user = usersRepository.getById(userId)
    val isLoading = MutableStateFlow(false)

    fun getData() {
        isLoading.value = true
        viewModelScope.launch {
            val usersDb = FirebaseObj.getData(DataConstants.FirebaseCollections.users)
            if (usersDb != null) {
                val usersConverted = usersDb.map {
                    val user = Users.firebaseMapToClass(it)
                    if (user.profilePic != null) {
                        user.profilePic = FirebaseObj.getImageUrl(user.profilePic!!)
                    }
                    user
                }
                usersRepository.deleteAll()
                usersRepository.insertList(usersConverted)

                // Get current user family house hold
                val familyHouseholdId =
                    usersConverted.firstOrNull { it.id == userId }?.familyHouseholdID
                // Create register of visit
                if (!familyHouseholdId.isNullOrEmpty()) {
                    createCheckIn(familyHouseholdId)
                }
            }

            val houseHoldVisitsDb =
                FirebaseObj.getData(DataConstants.FirebaseCollections.familyHouseholdVisits)
            if (houseHoldVisitsDb != null) {
                val houseHoldVisitsConverted =
                    houseHoldVisitsDb.map { FamilyHouseholdVisits.firebaseMapToClass(it) }
                householdVisitsRepository.deleteAll()
                householdVisitsRepository.insertList(houseHoldVisitsConverted)
            }

            val houseHoldTakenItemsDb =
                FirebaseObj.getData(DataConstants.FirebaseCollections.takenItems)
            if (houseHoldTakenItemsDb != null) {
                val houseHoldTakenItemsConverted =
                    houseHoldTakenItemsDb.map { TakenItems.firebaseMapToClass(it) }
                householdTakenItemsRepository.deleteAll()
                householdTakenItemsRepository.insertList(houseHoldTakenItemsConverted)
            }

            isLoading.value = false
        }
    }

    fun getVisitsMonthlyById(id: String): Flow<List<FamilyHouseholdVisits>> {
        return householdVisitsRepository.getVisitMonthlyById(id)
    }

    fun getTakenItensMonthlyById(id: String): Flow<Long> {
        return householdTakenItemsRepository.getTakenItensMonthlyById(id)
    }

    fun getHouseholdNotes(householdId: String): Flow<List<UsersNotes>> {
        return usersRepository.getHouseholdNotes(householdId)
    }

    private suspend fun createCheckIn(familyHouseholdId: String) {
        val mapToCreate = mapOf(
            "familyHouseholdId" to FirebaseObj.getReferenceById(
                DataConstants.FirebaseCollections.familyHousehold,
                familyHouseholdId
            ),
            // Cria explicitamente um Timestamp
            "date" to Date(System.currentTimeMillis())
        )

        FirebaseObj.insertData(
            DataConstants.FirebaseCollections.familyHouseholdVisits,
            mapToCreate
        )
    }
}