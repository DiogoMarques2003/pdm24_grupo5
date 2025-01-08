import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningDropdown(selectedColor: MutableState<String>, onWarningChanged: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextfieldElement(
            value = " ",
            onValueChange = {},
            readOnly = true,
            labelText = stringResource(R.string.warning_textfield),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            leadingIcon = DataConstants.mapStatusIcons[selectedColor.value],
            leadingIconColor = DataConstants.mapStatusColors[selectedColor.value] ?: Color.Unspecified,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DataConstants.StatusOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Icon(
                            imageVector = DataConstants.mapStatusIcons[option] ?: Icons.Default.Help,
                            contentDescription = "$option Status Icon",
                            tint = DataConstants.mapStatusColors[option] ?: Color.Gray
                        )
                    },
                    onClick = {
                        selectedColor.value = option
                        expanded = false
                        onWarningChanged(option)
                    }
                )
            }
        }
    }
}