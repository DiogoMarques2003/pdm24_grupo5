package com.kotlin.socialstore.viewModels.Donations

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.DonationsItemsRepository
import com.kotlin.socialstore.data.repository.DonationsRepository
import com.kotlin.socialstore.data.repository.StoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val storesRepository = StoresRepository(database.storesDao())

    private val donationItems = donationsItemsRepository.getByDonationId(donationId)
    val allCategories = categoriesRepository.allCategories
    val allStores = storesRepository.allStores

    val insertItems = MutableStateFlow<List<Stock>>(emptyList())
    val isLoading = MutableStateFlow(false)

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

    fun getData() {
        viewModelScope.launch {
            val categories =
                FirebaseObj.getData(DataConstants.FirebaseCollections.category) ?: return@launch
            val categoriesClass = categories.map { Category.firebaseMapToClass(it) }
            categoriesRepository.deleteAll()
            categoriesRepository.insertList(categoriesClass)

            val stores =
                FirebaseObj.getData(DataConstants.FirebaseCollections.stores) ?: return@launch
            val storesClass = stores.map { Stores.firebaseMapToClass(it) }
            storesRepository.deleteAll()
            storesRepository.insertList(storesClass)
        }
    }

    fun updateItemField(itemId: String, field: String, value: String?) {
        insertItems.value = insertItems.value.map { item ->
            if (item.id == itemId) {
                when (field) {
                    "picture" -> item.copy(picture = value)
                    "state" -> item.copy(state = value as String)
                    "size" -> item.copy(size = value)
                    "description" -> item.copy(description = value!!)
                    else -> item // Campo não encontrado, retorna o item sem alterações
                }
            } else {
                item
            }
        }
    }

    fun removeItem(itemId: String) {
        insertItems.value = insertItems.value.filter { it.id != itemId }
    }

    fun addItem() {
        insertItems.value += Stock(
            id = UUID.randomUUID().toString(),
            categoryID = "",
            picture = null,
            state = "",
            size = null,
            description = "",
            storesId = ""
        )
    }

    fun addItemsToStock(
        selectedItem: MutableState<String?>,
        defaultStorage: String?,
        context: Context
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val isValid = mutableStateOf(true)

            for (item in insertItems.first()) {
                if (item.categoryID.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.fill_category),
                        Toast.LENGTH_SHORT
                    ).show()
                    selectedItem.value = item.id
                    isValid.value = false
                    break
                }

                if (item.state.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.fill_state),
                        Toast.LENGTH_SHORT
                    ).show()
                    selectedItem.value = item.id
                    isValid.value = false
                    break
                }

                if (item.description.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.fill_description),
                        Toast.LENGTH_SHORT
                    ).show()
                    selectedItem.value = item.id
                    isValid.value = false
                    break
                }

                // Usar o defaultStorage se o item não tiver um storesId atribuído
                item.storesId = if (item.storesId.isBlank()) {
                    if (defaultStorage.isNullOrBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.fill_store),
                            Toast.LENGTH_SHORT
                        ).show()
                        selectedItem.value = item.id
                        isValid.value = false
                        break
                    } else {
                        defaultStorage
                    }
                } else {
                    item.storesId
                }
            }

            if (!isValid.value) {
                isLoading.value = false
                return@launch
            }

            // Insert data in the stock
            for (item in insertItems.first()) {
                if (!item.picture.isNullOrBlank()) {
                    // If start with https copy the imagem
                    item.picture = if (item.picture!!.startsWith("https://")) {
                        FirebaseObj.copyImageWithSameName(
                            item.picture!!,
                            DataConstants.FirebaseImageFolders.stock
                        )
                    } else {
                        FirebaseObj.createStorageImage(
                            Uri.parse(item.picture!!),
                            DataConstants.FirebaseImageFolders.stock
                        )
                    }
                }

                FirebaseObj.insertData(
                    DataConstants.FirebaseCollections.stock,
                    item.toFirebaseMap()
                )
            }

            // Update donation status
            val updateMapOf = mapOf("state" to DataConstants.donationDoneStatus)
            FirebaseObj.updateData(
                DataConstants.FirebaseCollections.donations,
                donationId,
                updateMapOf
            )

            Toast.makeText(
                context,
                context.getString(R.string.item_inserted_stock),
                Toast.LENGTH_LONG
            ).show()
            navController.popBackStack()
        }
    }
}