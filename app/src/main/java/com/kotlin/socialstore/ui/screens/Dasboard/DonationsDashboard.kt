import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun DonationsDashboard(donationsByMonth: Map<String, Int>) {
    Column(modifier = Modifier.padding(16.dp)) {
//        Text(text = "All time donations", modifier = Modifier.align(Alignment.CenterHorizontally))

        val maxDonationAmount = donationsByMonth.maxOfOrNull { it.value } ?: 1

        donationsByMonth.forEach { (month, amount) ->
            DonationBar(month = month, amount = amount, maxAmount = maxDonationAmount)
        }
    }
}

@Composable
fun DonationBar(month: String, amount: Int, maxAmount: Int) {
    val barWidth = (amount.toFloat() / maxAmount) * 200f  // Reduced max width to leave space for text

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = month)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)  // This ensures the bar takes available space
                    .height(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(barWidth.dp)
                        .background(
                            color = Color.Blue,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }

            Text(
                text = amount.toString(),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}