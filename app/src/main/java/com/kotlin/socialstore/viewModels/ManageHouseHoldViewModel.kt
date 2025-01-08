package com.kotlin.socialstore.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ManageHouseholdViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val usersDao = database.usersDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val familyHouseholdCollection = firestore.collection("familyHousehold")

    val _householdId = MutableStateFlow<String?>(null)

    private val _householdMembers = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val householdMembers: StateFlow<List<Map<String, Any>>> = _householdMembers

    var isInitialized = false
        private set

    fun initialize(currentUserId: String?) {
        if (currentUserId == null || isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            val user = usersDao.getById(currentUserId).firstOrNull()

            if (user?.familyHouseholdID.isNullOrEmpty()) {
                try {
                    val newHouseholdDocument = familyHouseholdCollection.document()
                    val newHouseholdData = mapOf("kidsStore" to false)
                    newHouseholdDocument.set(newHouseholdData).await()

                    val newFamilyHouseholdID = FirebaseObj.getReferenceById(
                        DataConstants.FirebaseCollections.familyHousehold,
                        newHouseholdDocument.id
                    )

                    firestore.collection("users").document(currentUserId)
                        .update("familyHouseholdID", newFamilyHouseholdID).await()

                    val updatedUser = user?.copy(familyHouseholdID = newFamilyHouseholdID.id)
                    if (updatedUser != null) {
                        usersDao.deleteById(user.id)
                        usersDao.insert(updatedUser)
                    }

                    _householdId.value = newFamilyHouseholdID.id
                    loadHouseholdMembers()
                } catch (e: Exception) {
                    _householdId.value = ""
                    Log.e("ManageHouseholdViewModel", "Erro ao criar/atualizar FamilyHousehold", e)
                }
            } else {
                _householdId.value = user?.familyHouseholdID
                loadHouseholdMembers()
            }
        }
    }

    private fun loadHouseholdMembers() {
        val id = _householdId.value ?: return

        viewModelScope.launch {
            try {
                val householdRef = FirebaseObj.getReferenceById("familyHousehold", id)
                val membersQuery = FirebaseObj.getData(
                    collection = "users",
                    whereField = "familyHouseholdID",
                    whereEqualTo = householdRef
                )

                if (!membersQuery.isNullOrEmpty()) {
                    _householdMembers.value = membersQuery.mapNotNull { userDocument ->
                        val userId = userDocument["id"] as? String
                        val userName = userDocument["name"] as? String ?: "Sem nome"
                        val profilePic = userDocument["profilePic"] as? String

                        if (userId != null) {
                            mapOf<String, Any>(
                                "id" to userId,
                                "name" to (userName ?: "Sem nome"),
                                "profilePic" to (profilePic ?: "")
                            )

                        } else null
                    }
                } else {
                    Log.e("ManageHouseholdViewModel", "Nenhum membro encontrado")
                }
            } catch (e: Exception) {
                Log.e("ManageHouseholdViewModel", "Erro ao carregar membros", e)
            }
        }
    }

    fun addUserToHousehold(email: String) {
        val id = _householdId.value ?: return

        viewModelScope.launch {
            try {
                val userQuery = FirebaseObj.getData("users", whereField = "email", whereEqualTo = email)
                if (!userQuery.isNullOrEmpty()) {
                    val userDocument = userQuery.first()
                    val userId = userDocument["id"] as String

                    val householdReference = firestore.collection("familyHousehold").document(id)
                    FirebaseObj.updateData("users", userId, mapOf("familyHouseholdID" to householdReference))

                    loadHouseholdMembers()
                } else {
                    Log.e("ManageHouseholdViewModel", "Usuário não encontrado.")
                }
            } catch (e: Exception) {
                Log.e("ManageHouseholdViewModel", "Erro ao adicionar usuário", e)
            }
        }
    }

    fun removeMemberFromHousehold(memberId: String) {
        val id = _householdId.value ?: return

        viewModelScope.launch {
            try {
                FirebaseObj.updateData("users", memberId, mapOf("familyHouseholdID" to ""))
                _householdMembers.value = _householdMembers.value.filterNot { it["id"] == memberId }
            } catch (e: Exception) {
                Log.e("ManageHouseholdViewModel", "Erro ao remover membro", e)
            }
        }
    }
}
