import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.kotlin.socialstore.ui.elements.PopBackButton

@Composable
fun TopBar(navController: NavController, title: String, showBackButoon: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = UiConstants.defaultPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(showBackButoon) {
            PopBackButton(navController)
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = UiConstants.titleTextSize,
            )
        }
    }
}
