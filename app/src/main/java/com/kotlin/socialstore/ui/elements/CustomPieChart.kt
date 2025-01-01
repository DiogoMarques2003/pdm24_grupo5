import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    data: Map<String, Int>,
    total: Int,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color.Green, Color.Red, Color.Blue, Color.Yellow
    )
) {
    val proportions = data.values.map { it.toFloat() / total }
    val segments = proportions.mapIndexed { index, proportion ->
        proportion to colors.getOrElse(index) { Color.Gray }
    }

    Canvas(modifier = modifier) {
        var startAngle = -90f
        segments.forEach { (proportion, color) ->
            val sweepAngle = proportion * 360f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 32.dp.toPx())
            )
            startAngle += sweepAngle
        }
    }
}
