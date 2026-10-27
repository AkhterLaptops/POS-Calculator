package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.TransactionEntity
import com.example.data.model.PosCalculationResult
import com.example.data.model.PosCategory
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryDialog(
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var showConfirmClear by remember { mutableStateOf(false) }

    val totalPosBilled = transactions.sumOf { it.posMachineAmount }
    val totalReceived = transactions.sumOf { it.amountReceived }
    val totalCharges = transactions.sumOf { it.totalCharges }

    val formatter = DecimalFormat("#,##0.00")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_history"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldMint,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Transaction History",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${transactions.size} records logged",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        if (transactions.isNotEmpty()) {
                            IconButton(onClick = { showConfirmClear = true }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear all history",
                                    tint = Color(0xFFDC3545)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Ribbon
                if (transactions.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL POS BILLED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "$currencySymbol ${formatter.format(totalPosBilled)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column {
                                Text(
                                    text = "NET RECEIVED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "$currencySymbol ${formatter.format(totalReceived)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                            Column {
                                Text(
                                    text = "TOTAL CHARGES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "$currencySymbol ${formatter.format(totalCharges)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBA4A00)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // List
                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PointOfSale,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No saved transactions yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap 'Save' on any calculation to log it here",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(transactions, key = { it.id }) { item ->
                            TransactionItemCard(
                                item = item,
                                currencySymbol = currencySymbol,
                                onDelete = { onDeleteTransaction(item.id) },
                                onShare = {
                                    val receipt = generateReceiptText(item, currencySymbol)
                                    shareText(context, receipt)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = { Text("Clear All History?") },
            text = { Text("Are you sure you want to permanently clear all saved POS transactions?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showConfirmClear = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClear = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TransactionItemCard(
    item: TransactionEntity,
    currencySymbol: String,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val formatter = DecimalFormat("#,##0.00")
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(item.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "POS Amount",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$currencySymbol ${formatter.format(item.posMachineAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary
                    )
                }

                Column {
                    Text(
                        text = "Net Received",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$currencySymbol ${formatter.format(item.amountReceived)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text(
                        text = "Charges (${String.format(Locale.US, "%.2f", item.ratePercent)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$currencySymbol ${formatter.format(item.totalCharges)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFBA4A00)
                    )
                }
            }

            if (item.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ref: ${item.note}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFDC3545).copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RatesConfigDialog(
    categories: List<PosCategory>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onUpdateCategory: (id: String, name: String, rate: Double) -> Unit,
    onAddNewCategory: (name: String, rate: Double) -> Unit,
    onDeleteCategory: (id: String) -> Unit,
    onResetDefaults: () -> Unit,
    onCurrencyChanged: (String) -> Unit
) {
    val editedNames = remember {
        mutableStateMapOf<String, String>().apply {
            categories.forEach { put(it.id, it.name) }
        }
    }
    val editedRates = remember {
        mutableStateMapOf<String, String>().apply {
            categories.forEach { put(it.id, String.format(Locale.US, "%.2f", it.defaultRatePercent)) }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newRateName by remember { mutableStateOf("") }
    var newRatePercent by remember { mutableStateOf("") }

    var selectedCurrency by remember { mutableStateOf(currencySymbol) }
    val currencies = listOf("₨", "$", "AED", "SAR", "€", "£")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_rates_config"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldMint,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Edit Rates & Payment Methods",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Configure bank merchant percentage rates",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Currency selector
                Text(
                    text = "Currency Symbol",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currencies.forEach { curr ->
                        val isSelected = selectedCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedCurrency = curr
                                    onCurrencyChanged(curr)
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = curr,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section Title + Add New Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Name & Percentage",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldMint,
                        modifier = Modifier.clickable { showAddDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Add New",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category List with EDITABLE Name and Rate
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories, key = { it.id }) { cat ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Point 5: Method Name is a full single line
                                OutlinedTextField(
                                    value = editedNames[cat.id] ?: cat.name,
                                    onValueChange = { newName ->
                                        editedNames[cat.id] = newName
                                        val currentRate = editedRates[cat.id]?.toDoubleOrNull() ?: cat.defaultRatePercent
                                        onUpdateCategory(cat.id, newName, currentRate)
                                    },
                                    label = { Text("Method Name", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                // Percentage and Delete take place below the Method Name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Rate % input matching home screen percentage typing box look & compact padding
                                    val currentRateStr = editedRates[cat.id] ?: String.format(Locale.US, "%.2f", cat.defaultRatePercent)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            EmeraldPrimary
                                        ),
                                        modifier = Modifier.width(115.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            BasicTextField(
                                                value = currentRateStr,
                                                onValueChange = { newVal ->
                                                    val sanitized = newVal.filter { it.isDigit() || it == '.' }
                                                    if (sanitized.count { it == '.' } <= 1 && sanitized.length <= 5) {
                                                        editedRates[cat.id] = sanitized
                                                        sanitized.toDoubleOrNull()?.let { parsedRate ->
                                                            val currentName = editedNames[cat.id] ?: cat.name
                                                            onUpdateCategory(cat.id, currentName, parsedRate)
                                                        }
                                                    }
                                                },
                                                singleLine = true,
                                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                                    textAlign = TextAlign.End,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = EmeraldAccent
                                                ),
                                                cursorBrush = SolidColor(EmeraldAccent),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                modifier = Modifier.weight(1f)
                                            )

                                            Text(
                                                text = "%",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = EmeraldAccent,
                                                modifier = Modifier.padding(start = 2.dp)
                                            )
                                        }
                                    }

                                    // Delete action button below the method name
                                    if (categories.size > 1) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFDC3545).copy(alpha = 0.1f),
                                            modifier = Modifier.clickable { onDeleteCategory(cat.id) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Rate",
                                                    tint = Color(0xFFDC3545),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Delete",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFDC3545)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            onResetDefaults()
                            categories.forEach {
                                editedNames[it.id] = it.name
                                editedRates[it.id] = String.format(Locale.US, "%.2f", it.defaultRatePercent)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Defaults")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Done")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "POS Calculator v2.2 • Android 9 to Latest (API 28 - 36)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    // Add New Rate Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Payment Method") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newRateName,
                        onValueChange = { newRateName = it },
                        label = { Text("Method Name") },
                        placeholder = { Text("e.g. Nayapay QR / HBL Card") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRatePercent,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() || it == '.' }
                            if (clean.count { it == '.' } <= 1) newRatePercent = clean
                        },
                        label = { Text("Rate Percentage (%)") },
                        placeholder = { Text("e.g. 2.50") },
                        trailingIcon = { Text("%") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rate = newRatePercent.toDoubleOrNull() ?: 2.0
                        if (newRateName.isNotBlank()) {
                            onAddNewCategory(newRateName, rate)
                            newRateName = ""
                            newRatePercent = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReceiptDialog(
    result: PosCalculationResult,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSaveToHistory: (note: String) -> Unit
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf("") }
    val receiptText = generateReceiptTextFromResult(result, currencySymbol, note)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_receipt"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldMint,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "POS Slip Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Slip Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "POINT OF SALE TRANSACTION SLIP",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptLineItem("Method", result.categoryName)
                        ReceiptLineItem("Criteria Mode", result.criteria.shortTitle)
                        ReceiptLineItem("Bank POS Rate", "${result.formatRate()}${if (result.isCustomPercentage) " (Custom)" else ""}")
                        ReceiptLineItem("Base Amount", result.formatCurrency(result.baseAmount, currencySymbol))

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        ReceiptLineItem(
                            label = "TYPE ON POS MACHINE",
                            value = result.formatCurrency(result.posMachineAmount, currencySymbol),
                            isBold = true,
                            isHighlighted = true
                        )
                        ReceiptLineItem(
                            label = "Amount Received",
                            value = result.formatCurrency(result.amountReceived, currencySymbol),
                            isBold = true
                        )
                        ReceiptLineItem(
                            label = "POS Bank Fee",
                            value = result.formatCurrency(result.totalCharges, currencySymbol),
                            isBold = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional note input
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Customer / Bill Reference (Optional)") },
                    placeholder = { Text("e.g. Invoice #1024, Ahmed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("POS Receipt", receiptText))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }

                    OutlinedButton(
                        onClick = {
                            shareText(context, receiptText)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = {
                            onSaveToHistory(note)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptLineItem(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall,
            color = if (isHighlighted) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodyMedium,
            color = if (isHighlighted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

fun generateReceiptTextFromResult(result: PosCalculationResult, currencySymbol: String, note: String): String {
    val sb = StringBuilder()
    sb.appendLine("💳 POS TRANSACTION SLIP")
    sb.appendLine("━━━━━━━━━━━━━━━━━━━━━")
    sb.appendLine("Date: ${SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date())}")
    sb.appendLine("Method: ${result.categoryName}")
    sb.appendLine("Mode: ${result.criteria.shortTitle}")
    sb.appendLine("Rate: ${result.formatRate()}")
    sb.appendLine("Base Amount: ${result.formatCurrency(result.baseAmount, currencySymbol)}")
    sb.appendLine("─────────────────────")
    sb.appendLine("TYPE ON POS: ${result.formatCurrency(result.posMachineAmount, currencySymbol)}")
    sb.appendLine("You Receive: ${result.formatCurrency(result.amountReceived, currencySymbol)}")
    sb.appendLine("POS Charges: ${result.formatCurrency(result.totalCharges, currencySymbol)}")
    sb.appendLine("─────────────────────")
    if (note.isNotBlank()) {
        sb.appendLine("Ref: $note")
    }
    sb.appendLine("Generated by POS Calculator")
    return sb.toString()
}

fun generateReceiptText(item: TransactionEntity, currencySymbol: String): String {
    val formatter = DecimalFormat("#,##0.00")
    val dateStr = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    val sb = StringBuilder()
    sb.appendLine("💳 POS TRANSACTION SLIP")
    sb.appendLine("━━━━━━━━━━━━━━━━━━━━━")
    sb.appendLine("Date: $dateStr")
    sb.appendLine("Method: ${item.categoryName}")
    sb.appendLine("Mode: ${item.criteriaName}")
    sb.appendLine("Rate: ${String.format(Locale.US, "%.2f", item.ratePercent)}%")
    sb.appendLine("Base Amount: $currencySymbol ${formatter.format(item.baseAmount)}")
    sb.appendLine("─────────────────────")
    sb.appendLine("TYPE ON POS: $currencySymbol ${formatter.format(item.posMachineAmount)}")
    sb.appendLine("You Receive: $currencySymbol ${formatter.format(item.amountReceived)}")
    sb.appendLine("POS Charges: $currencySymbol ${formatter.format(item.totalCharges)}")
    sb.appendLine("─────────────────────")
    if (item.note.isNotBlank()) {
        sb.appendLine("Ref: ${item.note}")
    }
    sb.appendLine("Generated by POS Calculator")
    return sb.toString()
}

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share POS Receipt"))
}
