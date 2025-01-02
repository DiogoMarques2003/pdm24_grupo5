import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

fun generateColors(size: Int): List<Color> {
    val baseColors = listOf(
        Color.Green, Color.Red, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
        Color.LightGray, Color.DarkGray, Color(0xFFFFA500), Color(0xFF800080) // Orange and Purple
    )

    return List(size) { index ->
        baseColors.getOrElse(index) {
            // Generate new colors using HSV when baseColors are exhausted
            val hsvColor = AndroidColor.HSVToColor(floatArrayOf((index * 30f) % 360, 1f, 1f))
            Color(hsvColor) // Convert the generated AndroidColor to Compose Color
        }
    }
}
