import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PieChart(
    data: Map<String, Int>,
    total: Int,
    modifier: Modifier = Modifier,
    colors: List<Color>
) {
    val proportions = data.values.map { it.toFloat() / total }
    val segments = proportions.mapIndexed { index, proportion ->
        proportion to colors.getOrElse(index) { Color.Gray }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            segments.forEach { (proportion, color) ->
                val sweepAngle = proportion * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }

        // Draw Total People in the Center
        Text(
            text = "$total",
            style = TextStyle(
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

