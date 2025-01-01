import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardData(
    val total: Int = 0,
    val nationalities: Map<String, Int> = emptyMap()
)

class DashboardViewModel(
    context: Context, val navController: NavController
) : ViewModel() {
    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            val nationalityCounts = mutableMapOf<String, Int>()
            val beneficiariesList = FirebaseObj.getData(DataConstants.FirebaseCollections.users, null, "accountType", DataConstants.AccountType.benefiaryy) ?: return@launch
            val beneficiariesConv = beneficiariesList.map { Users.firebaseMapToClass(it) }
            for (document in beneficiariesConv) {
                val nationality = document.nationality ?: "Unknown"
                nationalityCounts[nationality] = nationalityCounts.getOrDefault(nationality, 0) + 1
            }

            _dashboardData.value = DashboardData(
                total = nationalityCounts.values.sum(),
                nationalities = nationalityCounts
            )
        }
    }
}
