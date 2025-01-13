import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.Stock
import com.kotlin.socialstore.data.entity.TakenItems
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.CategoryRepository
import com.kotlin.socialstore.data.repository.StockRepository
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckOutViewModel(
    context: Context,
    private val userID: String
) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val productsRepository = StockRepository(database.stockDao())
    private val userRepository = UsersRepository(database.usersDao())

    private var categoryListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null

    val beneficiaryData = userRepository.getById(userID)
    val adminData = FirebaseObj.getCurrentUser()?.let { userRepository.getById(it.uid) }
    val allCategories = categoriesRepository.allCategories
    val allStock = productsRepository.allStock

    fun getData(context: Context) {
        categoryListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.category,
            null,
            { updateCategoriesListener(it) },
            { Toast.makeText(context, "Error loading categories", Toast.LENGTH_SHORT).show() }
        )

        productsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.stock,
            null,
            { updateProductsListener(it) },
            { Toast.makeText(context, "Error loading products", Toast.LENGTH_SHORT).show() }
        )

        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            userID,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Error loading user info", Toast.LENGTH_SHORT).show() }
        )
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?) {
        viewModelScope.launch {
            //Get user
            val user = users?.firstOrNull() ?: return@launch

            val userConv = Users.firebaseMapToClass(user)

            userRepository.deleteById(userConv.id)

            //Get Image
            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            userRepository.insert(userConv)
        }
    }

    private fun updateCategoriesListener(categoriesList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (categoriesList == null) {
                //Delete all local data
                return@launch categoriesRepository.deleteAll()
            }

            val categoriesConv = categoriesList.map { Category.firebaseMapToClass(it) }

            categoriesRepository.deleteAll()

            categoriesRepository.insertList(categoriesConv)
        }
    }

    private fun updateProductsListener(productsList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (productsList == null) {
                //Delete all local data
                return@launch productsRepository.deleteAll()
            }

            val productsConv = productsList.map {
                val stock = Stock.firebaseMapToClass(it)
                if (stock.picture != null) {
                    stock.picture = FirebaseObj.getImageUrl(stock.picture!!)
                }
                stock
            }

            productsRepository.deleteAll()

            productsRepository.insertList(productsConv)

        }
    }

    fun onCheckOut(itemsAdded: List<Stock>, context: Context) {
        viewModelScope.launch {
            data class ItemsByCategory(
                val categoryID: String,
                var quantity: Int,
                var stockList: List<String>
            )

            val itemsByCategoryList = mutableListOf<ItemsByCategory>()
            val admin = adminData?.first()
            val beneficiary = beneficiaryData.first()

//            for (stock in itemsAdded) {
//                var rowcategory =
//                    itemsByCategoryList.filter { it.categoryID == stock.categoryID }.firstOrNull()
//                if (rowcategory != null) {
//                    val quantity = rowcategory.quantity
//                    rowcategory.quantity = quantity + 1
//                    rowcategory.stockList += stock.id
//                } else {
//                    itemsByCategoryList +=
//                        ItemsByCategory(
//                            categoryID = stock.categoryID,
//                            quantity = 1,
//                            stockList = listOf(stock.id)
//                        )
//                }
//            }

            itemsAdded.groupBy { it.categoryID }.forEach { (categoryId, stocks) ->
                val existingCategory = itemsByCategoryList.find { it.categoryID == categoryId }

                if (existingCategory != null) {
                    existingCategory.apply {
                        quantity += stocks.size
                        stockList = stockList + stocks.map { it.id }
                    }
                } else {
                    itemsByCategoryList += ItemsByCategory(
                        categoryID = categoryId,
                        quantity = stocks.size,
                        stockList = stocks.map { it.id }
                    )

                }
            }

            itemsByCategoryList.forEach { item ->
                val takenItems = TakenItems(
                    id = "",
                    familyHouseholdID = beneficiary.familyHouseholdID ?: "",
                    voluntierID = admin?.id ?: "",
                    categoryID = item.categoryID,
                    quantity = item.quantity,
                    date = Date(System.currentTimeMillis())
                )

                FirebaseObj.insertData(
                    DataConstants.FirebaseCollections.takenItems,
                    takenItems.toFirebaseMap()
                )

                item.stockList.forEach { stockID ->
                    FirebaseObj.deleteData(DataConstants.FirebaseCollections.stock, stockID)
                }
            }


        }
    }

    fun stopListeners() {
        categoryListener?.remove()
        productsListener?.remove()
        userListener?.remove()
        categoryListener = null
        productsListener = null
        userListener = null
    }
}