import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.DonationsItems
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.StoresRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonationViewModel(context: Context) : ViewModel() {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val storesRepository = StoresRepository(database.storesDao())
    private val _donationItems = MutableStateFlow<List<DonationsItems>>(emptyList())
    val donationItems = _donationItems.asStateFlow()
    val allCategories = categoriesRepository.allCategories
    val allStores = storesRepository.allStores

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

    fun submitDonation(
        fullName: String,
        phoneNumber: String,
        phoneCountryCode: String,
        email: String,
        location: String
    ) = viewModelScope.launch {
        try {
            val donation = Donations(
                id = "",
                donaterName = fullName,
                email = email,
                phoneNumber = phoneNumber,
                phoneCountryCode = phoneCountryCode,
                donationScheduleID = "",
                state = ""
            )

            FirebaseObj.insertData(
                DataConstants.FirebaseCollections.donations,
                donation.toFirebaseMap()
            )

            // Clear items after successful submission
            _donationItems.value = emptyList()

        } catch (e: Exception) {

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

