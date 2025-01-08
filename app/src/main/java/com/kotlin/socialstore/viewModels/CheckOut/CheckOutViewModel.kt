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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckOutViewModel(
    context: Context,
    private val userID: String
) : ViewModel() {

    private val database = AppDatabase.getDatabase(context)
    private val userRepository = UsersRepository(database.usersDao())
    private var categoryListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null
    private val categoriesRepository = CategoryRepository(database.categoryDao())
    private val productsRepository = StockRepository(database.stockDao())

    private var userListener: ListenerRegistration? = null

    val userData = userRepository.getById(userID)
    val allCategories = categoriesRepository.allCategories
    val allStock = productsRepository.allStock

    // Using MutableStateSet for better set operations
    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems

    private val _isConfirmEnabled = MutableStateFlow(false)
    val isConfirmEnabled: StateFlow<Boolean> = _isConfirmEnabled

    // Track quantities separately
    private val _itemQuantities = MutableStateFlow<Map<String, Int>>(emptyMap())
    val itemQuantities: StateFlow<Map<String, Int>> = _itemQuantities

    fun isItemSelected(itemId: String): Boolean {
        return _selectedItems.value.contains(itemId)
    }

    fun toggleItemSelection(itemId: String) {
        val currentItems = _selectedItems.value.toMutableSet()
        val currentQuantities = _itemQuantities.value.toMutableMap()

        if (currentItems.contains(itemId)) {
            // Remove item
            currentItems.remove(itemId)
            currentQuantities.remove(itemId)
        } else {
            // Add item with default quantity
            currentItems.add(itemId)
            currentQuantities[itemId] = 1
        }

        _selectedItems.value = currentItems
        _itemQuantities.value = currentQuantities
        updateConfirmButtonState()
    }

    fun updateItemQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            _selectedItems.value = _selectedItems.value - itemId
            _itemQuantities.value = _itemQuantities.value - itemId
        } else {
            // Ensure item is in selected set and update quantity
            _selectedItems.value = _selectedItems.value + itemId
            _itemQuantities.value = _itemQuantities.value + (itemId to quantity)
        }
        updateConfirmButtonState()
    }

    private fun updateConfirmButtonState() {
        _isConfirmEnabled.value = _selectedItems.value.isNotEmpty()
    }

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
            val user = users?.firstOrNull() ?: return@launch
            val userConv = Users.firebaseMapToClass(user)
            userRepository.deleteById(userConv.id)
            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }
            userRepository.insert(userConv)
        }
    }

    private fun updateCategoriesListener(categoriesList: List<Map<String, Any>>?) {
        viewModelScope.launch {
            if (categoriesList == null) {
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

    fun onCheckOut() {
        viewModelScope.launch {
            val currentUser = userData.first() ?: return@launch

            _selectedItems.value.forEach { stockId ->
                val quantity = _itemQuantities.value[stockId] ?: 1
                val stockItem = allStock.first().find { it.id == stockId } ?: return@forEach

                val takenItem = userData.first().familyHouseholdID?.let {
                    TakenItems(
                        id = "",
                        familyHouseholdID = it,
                        categoryID = stockItem.categoryID,
                        voluntierID = currentUser.id,
                        quantity = quantity,
                        date = Date(System.currentTimeMillis())
                    )
                }

                if (takenItem != null) {
                    FirebaseObj.insertData(
                        DataConstants.FirebaseCollections.takenItems,
                        takenItem.toFirebaseMap()
                    )
                }

                FirebaseObj.deleteData(DataConstants.FirebaseCollections.stock, stockId)
            }

            // Clear selections after checkout
            _selectedItems.value = emptySet()
            _itemQuantities.value = emptyMap()
            updateConfirmButtonState()
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