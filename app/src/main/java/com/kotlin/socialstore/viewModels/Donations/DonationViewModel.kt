package com.kotlin.socialstore.viewModels.Donations

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.DonationSchedule
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.DonationScheduleRepository
import com.kotlin.socialstore.data.repository.DonationsItemsRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import com.squareup.wire.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Date

class DonationViewModel(context: Context, val navController: NavController) : ViewModel() {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val donationScheduleRepository = DonationScheduleRepository(database.donationScheduleDao())
    private val donationsItemsRepository = DonationsItemsRepository(database.donationsItemsDao())

    private val _donationItems = MutableStateFlow<List<DonationsItems>>(emptyList())

    val donations = donationsRepository.allDonations
    val donationItems = _donationItems.asStateFlow()
    val allCategories = categoriesRepository.allCategories
    val allLocations = donationScheduleRepository.allDonationSchedule

    init {
        getLocationsData()
    }

    fun addDonationItem(
        description: String,
        categoryID: String,
        size: String,
        quantity: Int,
        state: String,
        picture: String? = null
    ) {
        val newItem = DonationsItems(
            id = "",
            categoryID = categoryID,
            donationID = "",
            description = description,
            size = size,
            quantity = quantity,
            state = state,
            picture = picture
        )

        _donationItems.update { currentList ->
            currentList + newItem
        }
    }

    private fun getLocationsData() {
        viewModelScope.launch {
            try {
                val locationsList = FirebaseObj.getData(DataConstants.FirebaseCollections.donationSchedule) ?: return@launch
                val locationsConv = locationsList.map { DonationSchedule.firebaseMapToClass(it) }
                donationScheduleRepository.insertList(locationsConv)
            } catch (e: Exception) {

            }
        }
    }

    fun submitDonation(
        fullName: String,
        phoneNumber: String,
        phoneCountryCode: String,
        email: String,
        locationID: String
    ) = viewModelScope.launch {
        try {
            val currentId = FirebaseObj.getLastId(DataConstants.FirebaseCollections.donations, "donationId")?.toIntOrNull() ?: 0
            val newID = (currentId + 1).toString()
            val donation = Donations(
                id = "",
                donaterName = fullName,
                email = email,
                phoneNumber = phoneNumber,
                phoneCountryCode = phoneCountryCode,
                donationScheduleID = locationID,
                state = DataConstants.donationInitialStatus,
                creationDate = Date(System.currentTimeMillis()),
                donationId = newID
            )

            // Create Donation
            FirebaseObj.insertData(
                DataConstants.FirebaseCollections.donations,
                donation.toFirebaseMap()
            )

            // Create donation items
            _donationItems.value.forEach { item ->
                val donationItem = DonationsItems(
                    id = "",
                    donationID = newID,
                    categoryID = item.categoryID,
                    picture = item.picture,
                    state = item.state,
                    size = item.size,
                    description = item.description,
                    quantity = item.quantity
                )
                FirebaseObj.insertData(
                    DataConstants.FirebaseCollections.donationsItems,
                    donationItem.toFirebaseMap()
                )
            }

            navController.popBackStack()

        } catch (e: Exception) {
            // TODO
        }
    }

    fun uploadDonationImage(uri: Uri): Flow<Result<String>> = flow {
        try {
            val filename = FirebaseObj.createStorageImage(uri, DataConstants.FirebaseImageFolders.donations)
            if (filename != null) {
                emit(Result.success(filename))
            } else {
                emit(Result.failure(Exception("Failed to upload image")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun removeDonationItem(itemId: String) {
        _donationItems.update { currentList ->
            currentList.filter { it.id != itemId }
        }
    }
}

