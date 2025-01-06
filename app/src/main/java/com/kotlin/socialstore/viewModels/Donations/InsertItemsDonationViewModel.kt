package com.kotlin.socialstore.viewModels.Donations

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.DonationsItemsRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID

class InsertItemsDonationViewModel(
    context: Context,
    val navController: NavController,
    private val donationId: String
) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val donationsRepository = DonationsRepository(database.donationsDao())
    private val donationsItemsRepository = DonationsItemsRepository(database.donationsItemsDao())

    private val donationItems = donationsItemsRepository.getByDonationId(donationId)

    val insertItems = MutableStateFlow<List<Stock>>(emptyList())

    init {
        convertItems()
    }

    private fun convertItems() {
        viewModelScope.launch {
            for (item in donationItems.first()) {
                for (i in 0 until item.quantity) {
                    // Insert on list
                    insertItems.value += Stock(
                        id = UUID.randomUUID().toString(),
                        categoryID = item.categoryID,
                        picture = item.picture,
                        state = item.state,
                        size = item.size,
                        description = item.description ?: "",
                        storesId = ""
                    )
                }
            }
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

    suspend fun updateItemImage(itemId: String, image: String) {
        insertItems.value = insertItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(picture = FirebaseObj.getImageUrl(image))
            } else {
                item
            }
        }
    }
}