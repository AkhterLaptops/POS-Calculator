package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CategorySelector
import com.example.ui.components.CriteriaSelector
import com.example.ui.components.HistoryDialog
import com.example.ui.components.PosAmountInputSection
import com.example.ui.components.PosResultCards
import com.example.ui.components.RatesConfigDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.PosCalculatorViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosCalculatorScreen(
    viewModel: PosCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pos_launcher_icon_1791356608041),
                                contentDescription = "App Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "POS Calculator",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldMint
                                ) {
                                    Text(
                                        text = "v2.2",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                            }
                            Text(
                                text = "powered by AKHTER LAPTOPS",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // History Action
                    IconButton(
                        onClick = { viewModel.setShowHistoryDialog(true) },
                        modifier = Modifier.testTag("btn_top_history")
                    ) {
                        if (uiState.historyList.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = EmeraldPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("${uiState.historyList.size}")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Rates / Settings Action
                    IconButton(
                        onClick = { viewModel.setShowRatesDialog(true) },
                        modifier = Modifier.testTag("btn_top_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Rates & Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Criteria Mode Selector ("Deduct from Amount" vs "Add to Amount")
                CriteriaSelector(
                    selectedCriteria = uiState.criteria,
                    onCriteriaSelected = { viewModel.onSelectCriteria(it) }
                )

                // Base Transaction Amount Input
                PosAmountInputSection(
                    amountInput = uiState.amountInput,
                    onAmountChange = { viewModel.onAmountInputChange(it) },
                    currencySymbol = uiState.currencySymbol,
                    onQuickAdd = { viewModel.onQuickAdd(it) },
                    onClear = { viewModel.onClearAmount() }
                )

                // Heading: "Select Rate or Type Custom" with Custom % Input Cell + Dropdown Menu
                CategorySelector(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.onSelectCategory(it) },
                    customPercentageInput = uiState.customPercentageInput,
                    onCustomPercentageChanged = { viewModel.onCustomPercentageChange(it) },
                    onOpenRatesSettings = { viewModel.setShowRatesDialog(true) }
                )

                // The 3 Result Output Cards (POS Machine Amount, Net Received, Total Charges)
                PosResultCards(
                    result = uiState.calculationResult,
                    currencySymbol = uiState.currencySymbol,
                    onCopiedToClipboard = { viewModel.notifyUser(it) }
                )

                // Action Buttons (Save & Receipt Slip)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setShowReceiptDialog(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_view_slip"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Slip",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.saveCurrentCalculation() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_save_transaction"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Log",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (uiState.showHistoryDialog) {
        HistoryDialog(
            transactions = uiState.historyList,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { viewModel.setShowHistoryDialog(false) },
            onDeleteTransaction = { viewModel.deleteTransaction(it) },
            onClearAll = { viewModel.clearAllHistory() }
        )
    }

    if (uiState.showRatesConfigDialog) {
        RatesConfigDialog(
            categories = uiState.categories,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { viewModel.setShowRatesDialog(false) },
            onUpdateCategory = { id, name, rate -> viewModel.updateCategory(id, name, rate) },
            onAddNewCategory = { name, rate -> viewModel.addNewCategory(name, rate) },
            onDeleteCategory = { id -> viewModel.deleteCategory(id) },
            onResetDefaults = { viewModel.resetRatesToDefault() },
            onCurrencyChanged = { viewModel.setCurrency(it) }
        )
    }

    if (uiState.showReceiptDialog) {
        ReceiptDialog(
            result = uiState.calculationResult,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { viewModel.setShowReceiptDialog(false) },
            onSaveToHistory = { note -> viewModel.saveCurrentCalculation(note) }
        )
    }
}
