package com.kotlin.socialstore.viewModels

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
import kotlinx.coroutines.launch

class DonationDetailsViewModel(context: Context, private val donationId: String) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val donationsItemsRepository = DonationsItemsRepository(database.donationsItemsDao())
    private val donationScheduleRepository = DonationScheduleRepository(database.donationScheduleDao())

    private var donationListener: ListenerRegistration? = null

    val donationData = donationsRepository.getById(donationId)
    val donationItems = donationsItemsRepository.getByDonationId(donationId)
    val allDonationSchedule = donationScheduleRepository.allDonationSchedule

    fun getData(context: Context) {
        // Add listener to firebase
        donationListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.donations,
            donationId,
            {updateDonationsListener(it)},
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })

        viewModelScope.launch {
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

            val donationItemsConverted = donationItems.map { DonationsItems.firebaseMapToClass(it) }
            donationsItemsRepository.deleteAll()
            donationsItemsRepository.insertList(donationItemsConverted)

            val donationsScheduleConverted = donationsSchedule.map { DonationSchedule.firebaseMapToClass(it) }
            donationScheduleRepository.deleteAll()
            donationScheduleRepository.insertList(donationsScheduleConverted)
        }
    }

    fun stopListeners() {
        donationListener?.remove()
        donationListener = null
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