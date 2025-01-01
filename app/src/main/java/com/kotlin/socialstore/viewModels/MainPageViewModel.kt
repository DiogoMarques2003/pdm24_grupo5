package com.kotlin.socialstore.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.FamilyHouseholdVisits
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import com.kotlin.socialstore.data.repository.FamilyHouseholdVisitsRepository
import com.kotlin.socialstore.data.repository.StockRepository
import com.kotlin.socialstore.data.repository.TakenItemsRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class MainPageViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var userListener: ListenerRegistration? = null
    private val userRepository = UsersRepository(database.usersDao())
    private val productsRepository = StockRepository(database.stockDao())
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val householdVisitRepository =
        FamilyHouseholdVisitsRepository(database.familyHouseholdVisitsDao())
    private val takenItemsRepository = TakenItemsRepository(database.takenItemsDao())
    private val currUser = FirebaseObj.getCurrentUser()

    val userData = userRepository.getById(currUser!!.uid)
    val lastProducts = productsRepository.getLastRows(5)
    val lastDonations = donationsRepository.getLastDonations(10)
    val allCategories = categoriesRepository.allCategories
    var vistisMonthly = householdVisitRepository.getAllmonthly() //TODO: Refazer para um SELECT SUM

    fun getUserInfo(context: Context) {
        //Add Listener to User
        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            currUser?.uid,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    fun getDonations() {
        viewModelScope.launch {
            val donations = FirebaseObj.getData(DataConstants.FirebaseCollections.donations, null)

            if (donations!!.isEmpty()) {
                return@launch
            }

            val donationsConv = donations.map { Donations.firebaseMapToClass(it) }

            //delete all data
            donationsRepository.deleteAll()

            //Insert new data
            donationsRepository.insertList(donationsConv)
        }
    }

    fun getProdcuts() {
        viewModelScope.launch {
            val products =
                FirebaseObj.getData(DataConstants.FirebaseCollections.stock, null)

            if (products!!.isEmpty()) {
                return@launch
            }

            val prodcutsConv = products.map {
                val stock = Stock.firebaseMapToClass(it)
                if (stock.picture != null) {
                    stock.picture = FirebaseObj.getImageUrl(stock.picture!!)
                }
                stock
            }

            //delete all data
            productsRepository.deleteAll()

            //Insert new data
            productsRepository.insertList(prodcutsConv)
        }
    }

    fun getVisits() {
        viewModelScope.launch {
            val visits =
                FirebaseObj.getData(DataConstants.FirebaseCollections.familyHouseholdVisits, null)

            if (visits!!.isEmpty()) {
                return@launch
            }

            val visitsConv = visits.map { FamilyHouseholdVisits.firebaseMapToClass(it) }

            //delete all data
            householdVisitRepository.deleteAll()

            //Insert new data
            householdVisitRepository.insertList(visitsConv)
        }
    }

    fun getTakenItems() {
        viewModelScope.launch {
            val takenItems =
                FirebaseObj.getData(DataConstants.FirebaseCollections.takenItems, null)

            if (takenItems!!.isEmpty()) {
                return@launch
            }

            val takenItemsConv = takenItems.map { TakenItems.firebaseMapToClass(it) }

            //delete all data
            takenItemsRepository.deleteAll()

            //Insert new data
            takenItemsRepository.insertList(takenItemsConv)
        }
    }

    @Composable
    fun getPublishDays(date: Date, context: Context): String {
        val timestampAtual = System.currentTimeMillis() // Timestamp atual em milissegundos
        val diferencaMillis = timestampAtual - date.time // Diferença entre os timestamps

        val msDays = TimeUnit.MILLISECONDS.toDays(diferencaMillis)
        val msHours = TimeUnit.MILLISECONDS.toHours(diferencaMillis)

        return if (msHours < 1) {
            "${TimeUnit.MILLISECONDS.toMinutes(diferencaMillis)} ${context.getString(R.string.minutes_ago)}"
        } else if (msDays < 1) {
            "$msHours ${context.getString(R.string.hours_ago)}"
        } else {
            "$msDays ${context.getString(R.string.days_ago)}"
        }
    }

    fun getVisitsMonthlyById(id: String): Flow<List<FamilyHouseholdVisits>> {
        return householdVisitRepository.getVisitMonthlyById(id)
    }

    fun getTakenItensMonthlyById(id: String): Flow<Long>{
        return takenItemsRepository.getTakenItensMonthlyById(id)
    }

    fun getTakenItensMonthly(): Flow<Long>{
        return takenItemsRepository.getTakenItensMonthly()
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?) {
        viewModelScope.launch {
            //Get user
            val user = users?.firstOrNull() ?: return@launch

            //Convert firebase data to local db data
            val userConv = Users.firebaseMapToClass(user)

            //Delete local data
            userRepository.deleteById(userConv.id)

            //Get Image
            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            //Insert new data
            userRepository.insert(userConv)
        }
    }
}