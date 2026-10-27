package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationCriteria
import com.example.data.model.PosCategory
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary

@Composable
fun CriteriaSelector(
    selectedCriteria: CalculationCriteria,
    onCriteriaSelected: (CalculationCriteria) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculationCriteria.entries.forEach { criteria ->
                val isSelected = selectedCriteria == criteria
                val backgroundColor = if (isSelected) EmeraldPrimary else Color.Transparent
                val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(backgroundColor)
                        .clickable { onCriteriaSelected(criteria) }
                        .padding(vertical = 10.dp, horizontal = 8.dp)
                        .testTag("criteria_${criteria.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = criteria.shortTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<PosCategory>,
    selectedCategory: PosCategory,
    onCategorySelected: (PosCategory) -> Unit,
    customPercentageInput: String,
    onCustomPercentageChanged: (String) -> Unit,
    onOpenRatesSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val isCustomActive = customPercentageInput.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        // Point 2: Header Row with Title and Input Cell for custom percentage right in front
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Rate or Type Custom",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Direct Custom Percentage Input Cell in front of heading
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isCustomActive) EmeraldMint else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    1.dp,
                    if (isCustomActive) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier.width(115.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (customPercentageInput.isEmpty()) {
                            Text(
                                text = "Custom",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        BasicTextField(
                            value = customPercentageInput,
                            onValueChange = { onCustomPercentageChanged(it) },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                color = if (isCustomActive) EmeraldDark else MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(EmeraldPrimary),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_custom_percentage_cell")
                        )
                    }

                    Text(
                        text = "%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isCustomActive) EmeraldDark else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    if (isCustomActive) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Custom %",
                            tint = EmeraldDark,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onCustomPercentageChanged("") }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Point 2 & 3: Clean full-width Dropdown Menu below heading without icons or side button
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.5.dp,
                    if (isCustomActive) MaterialTheme.colorScheme.outline.copy(alpha = 0.4f) else EmeraldPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isDropdownExpanded = !isDropdownExpanded }
                    .testTag("dropdown_trigger_category")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedCategory.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isCustomActive)
                                "Rate: ${String.format(java.util.Locale.US, "%.2f", selectedCategory.defaultRatePercent)}% (Overridden by Custom %)"
                            else
                                "Rate: ${String.format(java.util.Locale.US, "%.2f", selectedCategory.defaultRatePercent)}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomActive) Color(0xFFBA4A00) else EmeraldPrimary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Expand Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Dropdown Menu Items (Clean without logos)
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory.id == category.id

                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) EmeraldMint else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.2f", category.defaultRatePercent)}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        onClick = {
                            onCategorySelected(category)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}

