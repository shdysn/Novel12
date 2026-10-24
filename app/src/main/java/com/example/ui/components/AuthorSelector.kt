package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.UrduFontFamily

/**
 * Reusable Author Selector with Dropdown for existing authors and a "+ New Author" option
 * to eliminate duplicate author entries across the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorSelector(
    selectedAuthor: String,
    onAuthorSelected: (String) -> Unit,
    existingAuthors: List<String>,
    modifier: Modifier = Modifier,
    label: String = "مصنف کا نام *"
) {
    // Unique sorted list of non-blank existing authors
    val cleanAuthorList = remember(existingAuthors) {
        existingAuthors
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var expanded by remember { mutableStateOf(false) }

    // Check if initial selectedAuthor is a custom/new author not in list
    var isAddNewMode by remember {
        mutableStateOf(selectedAuthor.isNotBlank() && !cleanAuthorList.contains(selectedAuthor.trim()))
    }
    var newAuthorInput by remember {
        mutableStateOf(if (isAddNewMode) selectedAuthor else "")
    }

    // Keep state in sync if parent changes initial value
    LaunchedEffect(selectedAuthor) {
        if (selectedAuthor.isNotBlank() && !cleanAuthorList.contains(selectedAuthor.trim()) && newAuthorInput.isBlank()) {
            isAddNewMode = true
            newAuthorInput = selectedAuthor
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val displayText = when {
                isAddNewMode -> "+ نیا مصنف شامل کریں (New Author)"
                selectedAuthor.isNotBlank() -> selectedAuthor
                else -> "موجودہ مصنف منتخب کریں..."
            }

            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                readOnly = true,
                label = { Text(label, fontFamily = UrduFontFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Option 1: Add New Author
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+ نیا مصنف شامل کریں (Add New Author)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                fontSize = 13.sp
                            )
                        }
                    },
                    onClick = {
                        isAddNewMode = true
                        expanded = false
                        onAuthorSelected(newAuthorInput)
                    }
                )

                if (cleanAuthorList.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    cleanAuthorList.forEach { authorName ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = authorName,
                                        fontFamily = UrduFontFamily,
                                        fontWeight = if (selectedAuthor == authorName && !isAddNewMode) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedAuthor == authorName && !isAddNewMode) GoldAccent else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                }
                            },
                            onClick = {
                                isAddNewMode = false
                                expanded = false
                                onAuthorSelected(authorName)
                            }
                        )
                    }
                }
            }
        }

        // Text field for New Author input when isAddNewMode is active
        if (isAddNewMode) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newAuthorInput,
                onValueChange = {
                    newAuthorInput = it
                    onAuthorSelected(it)
                },
                label = { Text("نیا مصنف کا نام درج کریں *", fontFamily = UrduFontFamily) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
