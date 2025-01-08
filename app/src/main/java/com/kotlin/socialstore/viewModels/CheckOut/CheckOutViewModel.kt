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
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val currUser = FirebaseObj.getCurrentUser()

    private var userListener: ListenerRegistration? = null

    val userData = userRepository.getById(userID)

    val allCategories = categoriesRepository.allCategories
    val allStock = productsRepository.allStock

    val selectedItemsSet = mutableSetOf<String>()
    val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()
    private val _isConfirmEnabled = MutableStateFlow(false)
    val isConfirmEnabled: StateFlow<Boolean> = _isConfirmEnabled

    fun getData(context: Context) {
        //Add listener to firebase
        categoryListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.category,
            null,
            { updateCategoriesListener(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })

        productsListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.stock,
            null,
            { updateProductsListener(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })

        userListener = FirebaseObj.listenToData(
            DataConstants.FirebaseCollections.users,
            userID,
            { updateUserInfo(it) },
            { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() })
    }

    private fun updateUserInfo(users: List<Map<String, Any>>?) {
        viewModelScope.launch {
            //Get user
            val user = users?.firstOrNull() ?: return@launch

            //Convert firebase data to local db data
            val userConv = Users.firebaseMapToClass(user)

            //Delete local data
            userRepository.deleteById(userConv.id)

            //Get Image
            if (userConv.profilePic != null) {
                userConv.profilePic = FirebaseObj.getImageUrl(userConv.profilePic!!)
            }

            //Insert new data
            userRepository.insert(userConv)
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

    fun toggleItemSelection(itemId: String) {
        _selectedItems.update { currentItems ->
            if (currentItems.contains(itemId)) {
                currentItems - itemId
            } else {
                currentItems + itemId
            }
        }

        _isConfirmEnabled.value = _selectedItems.value.isNotEmpty()
    }

    fun onCheckOut() {
        viewModelScope.launch {
            for (stockID in selectedItemsSet) {
                FirebaseObj.deleteData(DataConstants.FirebaseCollections.stock, stockID)

//                val updatedData = TakenItems(
//                    id = "",
//                    familyHouseholdID = userData,
//                    categoryID =
//                )
//                FirebaseObj.insertData(DataConstants.FirebaseCollections.takenItems, )
            }


        }

    }

    fun stopListeners() {
        categoryListener?.remove()
        categoryListener = null
        productsListener?.remove()
        productsListener = null
    }
}
