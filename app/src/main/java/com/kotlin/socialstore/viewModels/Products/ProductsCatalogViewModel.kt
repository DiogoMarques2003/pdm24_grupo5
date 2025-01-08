package com.kotlin.socialstore.viewModels.Products

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.StockRepository

import kotlinx.coroutines.launch


class ProductsCatalogViewModel(context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private var categoryListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val productsRepository = StockRepository(database.stockDao())

    val allCategories = categoriesRepository.allCategories
    val allStock = productsRepository.allStock

    fun getData(context: Context) {
        //Add listener to firebase
        categoryListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.category,
            null,
            { updateCategoriesListener(it) },
            { Toast.makeText(context,context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show() })

        productsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.stock,
            null,
            { updateProductsListener(it) },
            { Toast.makeText(context,context.getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show()
            })
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
                return@launch productsRepository.deleteAll()
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
            productsRepository.deleteAll()

            //Insert new data
            productsRepository.insertList(productsConv)

        }
    }

    fun stopListeners() {
        categoryListener?.remove()
        categoryListener = null
        productsListener?.remove()
        productsListener = null
    }
}