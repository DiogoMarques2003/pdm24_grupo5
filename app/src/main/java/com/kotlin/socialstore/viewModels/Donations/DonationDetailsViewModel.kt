package com.kotlin.socialstore.viewModels.Donations

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.DonationSchedule
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.DonationScheduleRepository
import com.kotlin.socialstore.data.repository.DonationsItemsRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DonationDetailsViewModel(context: Context, val donationId: String) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val donationsItemsRepository = DonationsItemsRepository(database.donationsItemsDao())
    private val donationScheduleRepository = DonationScheduleRepository(database.donationScheduleDao())

    private var donationListener: ListenerRegistration? = null

    val donationData = donationsRepository.getById(donationId)
    val donationItems = donationsItemsRepository.getByDonationId(donationId)
    val allDonationSchedule = donationScheduleRepository.allDonationSchedule

    var finishedLoadingData = MutableStateFlow(false)
    var isLoading = MutableStateFlow(false)

    fun getData(context: Context) {
        // Add listener to firebase
        donationListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.donations,
            donationId,
            {updateDonationsListener(it)},
            { Toast.makeText(context, context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show() })

        viewModelScope.launch {
            try {
                // Get donation items from firebase
                val donationItems = FirebaseObj.getData(DataConstants.FirebaseCollections.donationsItems)
                    ?: return@launch Toast.makeText(
                        context,
                        context.getString(R.string.donation_items_not_found),
                        Toast.LENGTH_SHORT,
                    ).show()

                val donationsSchedule = FirebaseObj.getData(DataConstants.FirebaseCollections.donationSchedule)
                    ?: return@launch Toast.makeText(
                        context,
                        context.getString(R.string.donation_schedule_not_found),
                        Toast.LENGTH_SHORT,
                    ).show()

                val donationItemsConverted = donationItems.map {
                    val donationItem = DonationsItems.firebaseMapToClass(it)
                    if (donationItem.picture != null) {
                        donationItem.picture = FirebaseObj.getImageUrl(donationItem.picture!!)
                    }
                    donationItem
                }
                donationsItemsRepository.deleteAll()
                donationsItemsRepository.insertList(donationItemsConverted)

                val donationsScheduleConverted = donationsSchedule.map { DonationSchedule.firebaseMapToClass(it) }
                donationScheduleRepository.deleteAll()
                donationScheduleRepository.insertList(donationsScheduleConverted)
            } finally {
                finishedLoadingData.value = true
            }
        }
    }

    fun stopListeners() {
        donationListener?.remove()
        donationListener = null
    }

    fun updateDonationStatus(status: String, context: Context) {
        isLoading.value = true
        viewModelScope.launch {
            val updateMapOf = mapOf(
                "state" to status
            )

            val success = FirebaseObj.updateData(
                DataConstants.FirebaseCollections.donations,
                donationId,
                updateMapOf
            )

            Toast.makeText(
                context,
                context.getString(if (success) R.string.donation_updated else R.string.donation_not_updated),
                Toast.LENGTH_SHORT,
            ).show()

            isLoading.value =  false
        }
    }

    private fun updateDonationsListener(donationsList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (donationsList == null) {
                // Delete all local data
                return@launch donationsRepository.deleteAll()
            }

            // Convert firebase data to local db data
            val donation = donationsList.first()
            val donationClass = Donations.firebaseMapToClass(donation)

            // Delete all local data
            donationsRepository.deleteAll()

            // Insert new data
            donationsRepository.insert(donationClass)
        }
    }
}