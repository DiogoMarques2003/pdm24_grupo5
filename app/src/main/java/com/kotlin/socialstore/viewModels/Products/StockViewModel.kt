package com.kotlin.socialstore.viewModels.Products

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.Stores
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.StockRepository
import com.kotlin.socialstore.data.repository.StoresRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StockViewModel(
    private val context: Context
) : ViewModel() {

    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val stockRepository = StockRepository(database.stockDao())
    private val storesRepository = StoresRepository(database.storesDao())
    private var categoryListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null
    private var storesListener: ListenerRegistration? = null

    val allStock = stockRepository.allStock
    val allCategories = categoriesRepository.allCategories
    val allStores = storesRepository.allStores

    var isLoading = MutableStateFlow(false)

    fun addStock(
        description: String,
        size: String?,
        quantity: Int,
        state: String,
        categoryID: String,
        picture: String? = null,
        storesId : String
    ) = viewModelScope.launch {
        try {

            val stocks = List(quantity) {
                Stock(
                    id = "",
                    categoryID = categoryID,
                    picture = picture,
                    state = state,
                    size = size,
                    description = description,
                    storesId = storesId
                )
            }

            stocks.forEach { stock ->
                FirebaseObj.insertData(
                    DataConstants.FirebaseCollections.stock,
                    stock.toFirebaseMap()
                )
                    ?: return@launch Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        } catch (e: Exception) {

        }
    }

    fun uploadStockImage(uri: Uri): Flow<Result<String>> = flow {
        try {
            val filename = FirebaseObj.createStorageImage(uri, DataConstants.FirebaseImageFolders.stock)
            if (filename != null) {
                emit(Result.success(filename))
            } else {
                emit(Result.failure(Exception("Failed to upload image")))
            }

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getData(context: Context) {
        //Add listener to firebase
        categoryListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.category,
            null,
            { updateCategoriesListener(it) },
            { Toast.makeText(context, context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show() })

        productsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.stock,
            null,
            { updateProductsListener(it) },
            { Toast.makeText(context, context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show()
            })

        storesListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.stores,
            null,
            { updateStoresListener(it) },
            { Toast.makeText(context, context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show() })
    }

    fun onDelete(item: Stock) {
        viewModelScope.launch {
            try {
                FirebaseObj.deleteData(DataConstants.FirebaseCollections.stock, item.id)
            } catch (e: Exception) {
                Toast.makeText(context,
                    context.getString(R.string.erro_ao_apagar_o_produto),
                    Toast.LENGTH_LONG).
                show()
            }
        }
    }

    fun editProduct(id: String, stock: Map<String, Any>) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                FirebaseObj.updateData(DataConstants.FirebaseCollections.stock, id, stock)
            } catch (e: Exception) {
                Toast.makeText(context,
                    context.getString(R.string.erro_ao_editar_o_produto),
                    Toast.LENGTH_LONG).
                show()
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun updateCategoriesListener(categoriesList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (categoriesList == null) {
                //Delete all local data
                return@launch categoriesRepository.deleteAll()
            }

            //Convert firebase data to local db data
            val categoriesConv = categoriesList.map { Category.firebaseMapToClass(it) }

            //Delete all local data
            categoriesRepository.deleteAll()

            //Insert new data
            categoriesRepository.insertList(categoriesConv)
        }
    }

    private fun updateProductsListener(productsList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (productsList == null) {
                //Delete all local data
                return@launch stockRepository.deleteAll()
            }

            //Convert firebase data to local db data
            val productsConv = productsList.map {
                val stock = Stock.firebaseMapToClass(it)
                if (stock.picture != null){
                    stock.picture =  FirebaseObj.getImageUrl(stock.picture!!)
                }
                stock
            }

            //Delete all local data
            stockRepository.deleteAll()

            //Insert new data
            stockRepository.insertList(productsConv)

        }
    }

    private fun updateStoresListener(storesList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (storesList == null) {
                //Delete all local data
                return@launch storesRepository.deleteAll()
            }

            //Convert firebase data to local db data
            val storesConv = storesList.map { Stores.firebaseMapToClass(it) }

            //Delete all local data
            storesRepository.deleteAll()

            //Insert new data
            storesRepository.insertList(storesConv)
        }
    }

    fun stopListeners() {
        categoryListener?.remove()
        categoryListener = null
        productsListener?.remove()
        productsListener = null
        storesListener?.remove()
        storesListener = null
    }
}

