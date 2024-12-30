import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StockViewModel(
    private val context: Context
) : ViewModel() {

    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val stockRepository = StockRepository(database.stockDao())
    private var categoryListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    val allCategories = categoriesRepository.allCategories

    init {
        setupFirebaseListeners()
    }

    private fun setupFirebaseListeners() {
        categoryListener = firestore.collection("category")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                viewModelScope.launch {
                    snapshot?.documents?.map { doc ->
                        Category(
                            id = doc.id,
                            nome = doc.getString("nome") ?: ""
                        )
                    }?.let { categories ->
                        categoriesRepository.insertList(categories)
                    }
                }
            }

        productsListener = firestore.collection("stock")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                viewModelScope.launch {
                    snapshot?.documents?.map { doc ->
                        Stock.firebaseMapToClass(doc.data ?: emptyMap())
                    }?.let { stocks ->
                        stockRepository.insertList(stocks)
                    }
                }
            }
    }

    fun addStock(
        description: String,
        size: String?,
        quantity: Int,
        state: String,
        categoryName: String,
        picture: String? = null
    ) = viewModelScope.launch {
        try {
            val storesId = auth.currentUser?.uid ?: return@launch

            val stocks = List(quantity) {
                Stock(
                    id = "",
                    categoryID = categoryName,
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
            val filename = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("stockImages/$filename")

            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            emit(Result.success(downloadUrl.toString()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun onCleared() {
        super.onCleared()
        categoryListener?.remove()
        productsListener?.remove()
    }
}

