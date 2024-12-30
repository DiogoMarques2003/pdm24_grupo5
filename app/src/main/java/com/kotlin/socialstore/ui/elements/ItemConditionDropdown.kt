import UiConstants.itemConditions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemConditionDropdown(
    selectedCondition: String,
    onConditionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextfieldElement(
            value = selectedCondition,
            onValueChange = {},
            readOnly = true,
            labelText = "Condition",
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            itemConditions.forEach { condition ->
                DropdownMenuItem(
                    text = { Text(condition) },
                    onClick = {
                        onConditionSelected(condition)
                        expanded = false
                    }
                )
            }
        }
    }
}