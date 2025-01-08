package com.kotlin.socialstore.ui.elements.dropDown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.kotlin.socialstore.R
import com.kotlin.socialstore.ui.elements.OutlinedTextfieldElement

@Composable
fun NationalityDropdown(
    selectedNationality: String,
    onNationalitySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val nationalitiesRaw = context.resources.getStringArray(R.array.nationalities)
    val nationalities = nationalitiesRaw.map { it.split("|") }
    val selectedNationalityName = nationalities.find { it[1] == selectedNationality }?.get(0) ?: ""
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var textFieldSize by remember { mutableStateOf(Size.Zero)}

    Box() {


        OutlinedTextfieldElement(
            value = selectedNationalityName,
            onValueChange = {},
            readOnly = true,
            labelText = stringResource(R.string.nationality_text_field),
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(with(LocalDensity.current){ textFieldSize.width.toDp() })
                .heightIn(max = 400.dp)
        ) {
            nationalities.forEach { (name, code) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onNationalitySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}
