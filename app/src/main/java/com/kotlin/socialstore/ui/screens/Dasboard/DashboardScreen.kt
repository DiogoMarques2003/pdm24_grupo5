import TopBar
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
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.PopBackButton

@Composable
fun DashboardPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel
) {
    var selectedOption by remember { mutableStateOf("") }

    //Background element
    BackgroundImageElement()

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //BackgroundImageElement()
        TopBar(navController, "Dashboard", true)

        Spacer(modifier = Modifier.height(8.dp))

        DashboardDropdown(
            selectedOption = selectedOption,
            onOptionSelected = { newOption ->
                selectedOption = newOption
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        when(selectedOption) {
            DataConstants.DashboardOptions.beneficiariesPieChart -> {
                NationalitiesDashboard(navController, viewModel, modifier)
            }
            DataConstants.DashboardOptions.donationsBarChart -> {
                DonationsDashboard(navController, viewModel, modifier)
            }
        }



    }
}
