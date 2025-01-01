import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Donations
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.data.firebase.FirebaseObj
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class NationalitiesDashboardData(
    val total: Int = 0,
    val nationalities: Map<String, Int> = emptyMap()
)

data class DonationsDashboardData(
    val donationsByMonth: Map<String, Int> = emptyMap()
)

class DashboardViewModel(
    context: Context, val navController: NavController
) : ViewModel() {
    private val _nationalitiesDashboardData = MutableStateFlow(NationalitiesDashboardData())
    val nationalitiesDashboardData: StateFlow<NationalitiesDashboardData> = _nationalitiesDashboardData

    private val _donationsDashboardData = MutableStateFlow(DonationsDashboardData())
    val donationsDashboardData: StateFlow<DonationsDashboardData> = _donationsDashboardData

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchDonations()
        fetchNationalities()
    }

    private fun fetchNationalities() {
        viewModelScope.launch {
            val nationalityCounts = mutableMapOf<String, Int>()
            val beneficiariesList = FirebaseObj.getData(DataConstants.FirebaseCollections.users, null, "accountType", DataConstants.AccountType.benefiaryy) ?: return@launch
            val beneficiariesConv = beneficiariesList.map { Users.firebaseMapToClass(it) }
            for (document in beneficiariesConv) {
                val nationality = document.nationality ?: "Unknown"
                nationalityCounts[nationality] = nationalityCounts.getOrDefault(nationality, 0) + 1
            }

            _nationalitiesDashboardData.value = NationalitiesDashboardData(
                total = nationalityCounts.values.sum(),
                nationalities = nationalityCounts
            )
        }
    }

    private fun fetchDonations() {
        viewModelScope.launch {
            val donationCounts = mutableMapOf<String, Int>()
            val donationsList = FirebaseObj.getData(DataConstants.FirebaseCollections.donations, null) ?: return@launch
            val donationsListConv = donationsList.map { Donations.firebaseMapToClass(it) }
            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

            // First, collect all unique dates and sort them
            val sortedDates = donationsListConv
                .mapNotNull { it.creationDate }
                .distinctBy { dateFormat.format(it) }
                .sortedByDescending { it }

            // Create map entries in order
            sortedDates.forEach { date ->
                val monthYear = dateFormat.format(date)
                donationCounts[monthYear] = 0
            }

            // Now count the donations
            for (document in donationsListConv) {
                val date = document.creationDate
                if (date != null) {
                    val monthYear = dateFormat.format(date)
                    donationCounts[monthYear] = donationCounts.getOrDefault(monthYear, 0) + 1
                } else {
                    donationCounts["Unknown"] = donationCounts.getOrDefault("Unknown", 0) + 1
                }
            }


//            for (document in donationsListConv) {
//                // Get the date and format it to month-year
//                val date = document.creationDate
//                if (date != null) {
//                    val calendar = Calendar.getInstance()
//                    calendar.time = date
//
//                    val month = calendar.get(Calendar.MONTH) // 0-11
//                    val year = calendar.get(Calendar.YEAR)
//
//                    // Format as "January 2024" or use SimpleDateFormat
//                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(date)
//                    // Or shorter format: "Jan 2024"
//                    // val monthName = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(date)
//
//                    donationCounts[monthName] = donationCounts.getOrDefault(monthName, 0) + 1
//                } else {
//                    donationCounts["Unknown"] = donationCounts.getOrDefault("Unknown", 0) + 1
//                }
//            }

            _donationsDashboardData.value = DonationsDashboardData(
                donationsByMonth = donationCounts
            )
        }
    }
}
