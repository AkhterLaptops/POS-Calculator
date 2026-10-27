package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

@Composable
fun PosAmountInputSection(
    amountInput: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String,
    onQuickAdd: (Double) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "BASE TRANSACTION AMOUNT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Display / Standard Gboard-friendly Input
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (amountInput.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            )
                        }

                        BasicTextField(
                            value = amountInput,
                            onValueChange = { onAmountChange(it) },
                            textStyle = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = FontFamily.SansSerif
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(EmeraldPrimary),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_base_amount")
                        )
                    }

                    if (amountInput.isNotEmpty()) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear amount",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Add Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(500.0, 1000.0, 2000.0, 5000.0, 10000.0, 25000.0).forEach { addVal ->
                    val label = when {
                        addVal >= 1000.0 && addVal % 1000.0 == 0.0 -> "+${(addVal / 1000).toInt()}k"
                        else -> "+${addVal.toInt()}"
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onQuickAdd(addVal) }
                            .testTag("quick_add_$label")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDC3545).copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClear() }
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC3545),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
