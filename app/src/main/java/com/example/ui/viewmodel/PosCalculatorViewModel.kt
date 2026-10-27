package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TransactionEntity
import com.example.data.model.CalculationCriteria
import com.example.data.model.DEFAULT_POS_CATEGORIES
import com.example.data.model.PosCalculatorEngine
import com.example.data.model.PosCalculationResult
import com.example.data.model.PosCategory
import com.example.data.repository.PosRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PosUiState(
    val amountInput: String = "1000",
    val customPercentageInput: String = "",
    val isCustomPercentageActive: Boolean = false,
    val selectedCategory: PosCategory = DEFAULT_POS_CATEGORIES[0],
    val categories: List<PosCategory> = DEFAULT_POS_CATEGORIES,
    val criteria: CalculationCriteria = CalculationCriteria.DEDUCT_FROM_AMOUNT,
    val currencySymbol: String = "₨",
    val calculationResult: PosCalculationResult = PosCalculatorEngine.calculate(
        amount = 1000.0,
        selectedCategory = DEFAULT_POS_CATEGORIES[0],
        customPercentage = null,
        criteria = CalculationCriteria.DEDUCT_FROM_AMOUNT
    ),
    val showHistoryDialog: Boolean = false,
    val showRatesConfigDialog: Boolean = false,
    val showReceiptDialog: Boolean = false,
    val historyList: List<TransactionEntity> = emptyList()
)

class PosCalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PosRepository(application)

    private val _uiState = MutableStateFlow(
        PosUiState(currencySymbol = repository.getCurrencySymbol())
    )
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }

        viewModelScope.launch {
            repository.categories.collectLatest { catList ->
                _uiState.update { current ->
                    val updatedSelected = catList.find { it.id == current.selectedCategory.id }
                        ?: catList.firstOrNull() ?: current.selectedCategory
                    val newState = current.copy(
                        categories = catList,
                        selectedCategory = updatedSelected
                    )
                    newState.copy(calculationResult = computeResult(newState))
                }
            }
        }

        viewModelScope.launch {
            repository.transactions.collectLatest { txList ->
                _uiState.update { it.copy(historyList = txList) }
            }
        }
    }

    private fun computeResult(state: PosUiState): PosCalculationResult {
        val amount = state.amountInput.replace(",", "").toDoubleOrNull() ?: 0.0
        val customRate = if (state.isCustomPercentageActive) {
            state.customPercentageInput.toDoubleOrNull()
        } else null

        return PosCalculatorEngine.calculate(
            amount = amount,
            selectedCategory = state.selectedCategory,
            customPercentage = customRate,
            criteria = state.criteria
        )
    }

    fun onAmountInputChange(input: String) {
        val sanitized = input.filter { it.isDigit() || it == '.' }
        if (sanitized.count { it == '.' } > 1) return
        if (sanitized.length > 12) return

        _uiState.update { current ->
            val updated = current.copy(amountInput = sanitized)
            updated.copy(calculationResult = computeResult(updated))
        }
    }

    fun onClearAmount() {
        onAmountInputChange("")
    }

    fun onQuickAdd(amountToAdd: Double) {
        val current = _uiState.value.amountInput.replace(",", "").toDoubleOrNull() ?: 0.0
        val newAmount = current + amountToAdd
        val formatted = if (newAmount % 1.0 == 0.0) {
            newAmount.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", newAmount)
        }
        onAmountInputChange(formatted)
    }

    fun onCustomPercentageChange(percentStr: String) {
        val sanitized = percentStr.filter { it.isDigit() || it == '.' }
        if (sanitized.count { it == '.' } > 1) return
        if (sanitized.length > 5) return

        _uiState.update { current ->
            val updated = current.copy(
                customPercentageInput = sanitized,
                isCustomPercentageActive = sanitized.isNotEmpty()
            )
            updated.copy(calculationResult = computeResult(updated))
        }
    }

    fun onToggleCustomPercentage(active: Boolean) {
        _uiState.update { current ->
            val updated = current.copy(isCustomPercentageActive = active)
            updated.copy(calculationResult = computeResult(updated))
        }
    }

    fun onSelectCategory(category: PosCategory) {
        _uiState.update { current ->
            val updated = current.copy(
                selectedCategory = category,
                customPercentageInput = "",
                isCustomPercentageActive = false
            )
            updated.copy(calculationResult = computeResult(updated))
        }
    }

    fun onSelectCriteria(criteria: CalculationCriteria) {
        _uiState.update { current ->
            val updated = current.copy(criteria = criteria)
            updated.copy(calculationResult = computeResult(updated))
        }
    }

    fun setShowHistoryDialog(show: Boolean) {
        _uiState.update { it.copy(showHistoryDialog = show) }
    }

    fun setShowRatesDialog(show: Boolean) {
        _uiState.update { it.copy(showRatesConfigDialog = show) }
    }

    fun setShowReceiptDialog(show: Boolean) {
        _uiState.update { it.copy(showReceiptDialog = show) }
    }

    fun saveCurrentCalculation(note: String = "") {
        viewModelScope.launch {
            val result = _uiState.value.calculationResult
            if (result.baseAmount <= 0.0) {
                _snackbarEvent.emit("Enter an amount first")
                return@launch
            }
            val entity = TransactionEntity(
                baseAmount = result.baseAmount,
                categoryName = result.categoryName + if (result.isCustomPercentage) " (Custom Rate)" else "",
                ratePercent = result.effectiveRatePercent,
                criteriaName = result.criteria.displayName,
                posMachineAmount = result.posMachineAmount,
                amountReceived = result.amountReceived,
                totalCharges = result.totalCharges,
                note = note.trim()
            )
            repository.saveTransaction(entity)
            _snackbarEvent.emit("Saved to Transaction History")
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _snackbarEvent.emit("Transaction deleted")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllTransactions()
            _snackbarEvent.emit("All transaction history cleared")
        }
    }

    fun updateCategory(id: String, newName: String, newRatePercent: Double) {
        viewModelScope.launch {
            repository.updateCategory(id, newName, newRatePercent)
            _snackbarEvent.emit("Rate '$newName' updated to $newRatePercent%")
        }
    }

    fun addNewCategory(name: String, ratePercent: Double) {
        viewModelScope.launch {
            repository.addNewCategory(name, ratePercent)
            _snackbarEvent.emit("Added new rate: $name ($ratePercent%)")
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
            _snackbarEvent.emit("Category removed")
        }
    }

    fun resetRatesToDefault() {
        viewModelScope.launch {
            repository.resetCategoriesToDefault()
            _snackbarEvent.emit("All rates reset to default")
        }
    }

    fun setCurrency(symbol: String) {
        repository.setCurrencySymbol(symbol)
        _uiState.update { it.copy(currencySymbol = symbol) }
    }

    fun notifyUser(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }
}
