import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.kotlin.socialstore.R


@Composable
fun NationalitiesDashboard(
    navController: NavController,
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val nationalitiesDashboardData by viewModel.nationalitiesDashboardData.collectAsState()

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
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

}