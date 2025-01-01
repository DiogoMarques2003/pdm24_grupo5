import android.provider.ContactsContract.Data
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.ui.elements.TitleTextElement

@Composable
fun DashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel
) {
    val nationalitiesDashboardData by viewModel.nationalitiesDashboardData.collectAsState()
    val donationsDashboardData by viewModel.donationsDashboardData.collectAsState()
    var selectedOption by remember { mutableStateOf("") }
    val dashboardOptions = DataConstants.DashboardOptionsList

    // Dynamically assign unique colors to nationalities
    val colorMap = remember(nationalitiesDashboardData.nationalities) {
        val nationalities = nationalitiesDashboardData.nationalities.keys.toList()
        val colors = generateColors(nationalities.size)
        nationalities.zip(colors).toMap()
    }

    val nationalitiesRaw = LocalContext.current.resources.getStringArray(R.array.nationalities)
    val nationalityMap = nationalitiesRaw
        .map { it.split("|") }
        .associate { it[1] to it[0] }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title Section
        TitleTextElement(text = "Dashboard")
//        Text(
//            text = "Dashboard",
//            style = MaterialTheme.typography.headlineLarge,
//            color = MaterialTheme.colorScheme.primary
//        )

        Spacer(modifier = Modifier.height(8.dp))

        DashboardDropdown(
            options = dashboardOptions,
            selectedOption = selectedOption,
            onOptionSelected = { newOption ->
                selectedOption = newOption
            },
            modifier = Modifier.fillMaxWidth()
        )

//        Text(
//            text = "Overview of Beneficiary Nationalities",
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.onBackground
//        )

        Spacer(modifier = Modifier.height(24.dp))

        when(selectedOption) {
            DataConstants.DashboardOptions.beneficiariesPieChart -> {
                PieChart(
                    data = nationalitiesDashboardData.nationalities,
                    total = nationalitiesDashboardData.total,
                    modifier = modifier.size(250.dp),
                    colors = colorMap.values.toList()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Breakdown of Nationalities with Corresponding Colors
                nationalitiesDashboardData.nationalities.forEach { (nationality, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 8.dp)
                                    .background(colorMap[nationality] ?: Color.Gray)
                            )
                            Text(
                                text = nationalityMap[nationality] ?: nationality,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            DataConstants.DashboardOptions.donationsBarChart -> {
                val donationsByMonth = donationsDashboardData.donationsByMonth
                DonationsDashboard(donationsByMonth = donationsByMonth)
            }
        }



    }
}
