import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ListenerRegistration
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.entity.Category
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.data.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserManagementState(
    val users: List<Users> = emptyList(),
    val filteredUsers: List<Users> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val userType: String = "VOL",
    val selectedTabIndex: Int = 0
)

class ManageUsersViewModel(
    context: Context, val navController: NavController
) : ViewModel() {

    private val database: AppDatabase = AppDatabase.getDatabase(context)
    val usersRepository = UsersRepository(database.usersDao())
    private val _userManagementState = MutableStateFlow(UserManagementState())
    val userManagementState: StateFlow<UserManagementState> = _userManagementState

    private var usersListener: ListenerRegistration? = null

    init {
        fetchAllData(context)
    }

    fun setUserType(type: String) {
        _userManagementState.value = _userManagementState.value.copy(userType = type)
        filterUsers()
    }

    fun setSearchQuery(query: String) {
        _userManagementState.value = _userManagementState.value.copy(searchQuery = query)
        filterUsers()
    }

    fun updateUsersListener(usersList: List<Map<String, Any>>?, context: Context) {
        viewModelScope.launch {
            try {
                if (usersList == null) {
                    return@launch usersRepository.deleteAll()
                }

                val usersConv = usersList.map {
                    Users.firebaseMapToClass(it).copy(
                        profilePic = (it["profilePic"]?.toString()?.let { FirebaseObj.getImageUrl(it) })
                    )
                }


                usersRepository.deleteAll()
                usersRepository.insertList(usersConv)

                _userManagementState.value = _userManagementState.value.copy(users = usersConv)
                filterUsers()

            } catch (e: Exception) {
                Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
            } finally {
                _userManagementState.value = _userManagementState.value.copy(isLoading = false)
            }
        }
    }

    private fun fetchAllData(context: Context) {
        viewModelScope.launch {
            _userManagementState.value = _userManagementState.value.copy(isLoading = true)

            usersListener = FirebaseObj.listenToData(
                DataConstants.FirebaseCollections.users,
                null,
                { updateUsersListener(it, context) },
                { Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    fun setTabIndex(index: Int) {
        _userManagementState.value = _userManagementState.value.copy(selectedTabIndex = index)
        val userType = when (index) {
            0 -> "VOL"
            1 -> "BEN"
            else -> ""
        }
        setUserType(userType)
    }

    private fun filterUsers() {
        val allUsers = _userManagementState.value.users
        val userType = _userManagementState.value.userType
        val query = _userManagementState.value.searchQuery

        val filteredUsers = allUsers.filter {
            it.accountType.equals(userType, ignoreCase = true) &&
                    (query.isEmpty() || it.email.contains(query, ignoreCase = true))
        }

        _userManagementState.value = _userManagementState.value.copy(filteredUsers = filteredUsers)
    }

    fun updateUserStatus(userId: String, choice: Boolean) {
        viewModelScope.launch {
            try {
                if(choice == false) {
                    deleteUser(userId)
                } else if(choice == true) {
                    val updatedUser = _userManagementState.value.users.find { it.id == userId }?.copy(active = choice)
                    if (updatedUser != null) {
                        FirebaseObj.updateData(DataConstants.FirebaseCollections.users, userId, updatedUser.toFirebaseMap())
                    }
                }
                //fetchAllData(navController.context)
            } catch (e: Exception) {
                Toast.makeText(navController.context, "Error updating user status", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUser(userId: String) {
        viewModelScope.launch {
            FirebaseObj.deleteData(DataConstants.FirebaseCollections.users, userId)
        }

    }
}
