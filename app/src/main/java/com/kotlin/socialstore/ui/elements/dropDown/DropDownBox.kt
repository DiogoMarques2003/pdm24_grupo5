package com.kotlin.socialstore.ui.elements.dropDown

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownBox(
    expanded: MutableState<Boolean>,
    selectedRow: MutableState<String>,
    label: String,
    listItems: @Composable () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it }
    ) {
        OutlinedTextfieldElement(
            value = selectedRow.value,
            onValueChange = {},
            readOnly = true,
            labelText = label,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            listItems()
        }
    }
}