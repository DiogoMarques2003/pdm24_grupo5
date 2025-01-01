package com.kotlin.socialstore.viewModels.Donations

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.DonationsRepository
import kotlinx.coroutines.launch

class ListDonationsViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private var donationsListener: ListenerRegistration? = null

    val allDonnations = donationsRepository.allDonations

    fun getData(context: Context) {
        // Add listener to firebase
        donationsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.donations,
            null,
            {updateDonationsListener(it)},
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    private fun updateDonationsListener(donationsList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (donationsList == null) {
                // Delete all local data
                return@launch donationsRepository.deleteAll()
            }

            // Convert firebase data to local db data
            val donationsConverted = donationsList.map { Donations.firebaseMapToClass(it) }

            // Delete all local data
            donationsRepository.deleteAll()

            // Insert new data
            donationsRepository.insertList(donationsConverted)
        }
    }

    fun stopListeners() {
        donationsListener?.remove()
        donationsListener = null
    }
}