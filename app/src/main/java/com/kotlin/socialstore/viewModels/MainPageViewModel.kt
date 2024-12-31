package com.kotlin.socialstore.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import com.kotlin.socialstore.data.repository.FamilyHouseholdRepository
import com.kotlin.socialstore.data.repository.FamilyHouseholdVisitsRepository
import com.kotlin.socialstore.data.repository.StockRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.launch

class MainPageViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var userListener : ListenerRegistration? = null
    private val userRepository = UsersRepository(database.usersDao())
    private val productsRepository = StockRepository(database.stockDao())
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val householdVisitRepository = FamilyHouseholdVisitsRepository(database.familyHouseholdVisitsDao())
    private val currUser = FirebaseObj.getCurrentUser()

    val userData = userRepository.getById(currUser!!.uid)
    val lastProducts = productsRepository.getLastRows(5)
    val lastDonations = donationsRepository.getLastDonations(5)
    val allCategories = categoriesRepository.allCategories
    var lastVisits = householdVisitRepository.getAllmonthly()

    fun getUserInfo(context: Context){
        //Add Listener to User
        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            currUser?.uid,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    fun getDonations(context: Context){
        viewModelScope.launch {
           val donations =  FirebaseObj.getData(DataConstants.FirebaseCollections.donations,null)

            if (donations!!.isEmpty()){
                return@launch
            }

            val donationsConv = donations.map { Donations.firebaseMapToClass(it) }

            //delete all data
            donationsRepository.deleteAll()

            //Insert new data
            donationsRepository.insertList(donationsConv)
        }
    }

    fun getVisits(context: Context){
        viewModelScope.launch {
            val visits =  FirebaseObj.getData(DataConstants.FirebaseCollections.familyHouseholdVisits,null)

            if (visits!!.isEmpty()){
                return@launch
            }

            val visitsConv = visits.map { FamilyHouseholdVisits.firebaseMapToClass(it) }

            //delete all data
            householdVisitRepository.deleteAll()

            //Insert new data
            householdVisitRepository.insertList(visitsConv)

            lastVisits = householdVisitRepository.getAllmonthly()
        }
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