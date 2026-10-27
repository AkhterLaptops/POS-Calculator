package com.example.data.model

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

enum class CalculationCriteria(val displayName: String, val shortTitle: String, val explanation: String) {
    DEDUCT_FROM_AMOUNT(
        displayName = "Deduct from Above Amount",
        shortTitle = "Deduct from Amount",
        explanation = "Customer pays base amount on POS. Bank/POS fee is deducted from your received payout."
    ),
    ADD_TO_AMOUNT(
        displayName = "Add in the above Amount",
        shortTitle = "Add to Amount",
        explanation = "Charges are added to the POS machine. You receive the exact original amount in hand."
    );

    companion object {
        fun fromDisplayName(name: String): CalculationCriteria {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: DEDUCT_FROM_AMOUNT
        }
    }
}

data class PosCategory(
    val id: String,
    val name: String,
    val defaultRatePercent: Double,
    val iconType: String = "card"
)

val DEFAULT_POS_CATEGORIES = listOf(
    PosCategory(
        id = "pos_customer",
        name = "POS Charges for Customer",
        defaultRatePercent = 2.50,
        iconType = "card"
    ),
    PosCategory(
        id = "pos_shopkeepers",
        name = "POS Charges for Other Shopkeepers",
        defaultRatePercent = 3.00,
        iconType = "shop"
    ),
    PosCategory(
        id = "pos_international",
        name = "POS Charges on International Cards",
        defaultRatePercent = 4.50,
        iconType = "card"
    )
)

data class PosCalculationResult(
    val baseAmount: Double,
    val effectiveRatePercent: Double,
    val isCustomPercentage: Boolean,
    val criteria: CalculationCriteria,
    val categoryName: String,
    val posMachineAmount: Double,
    val amountReceived: Double,
    val totalCharges: Double
) {
    fun formatCurrency(amount: Double, symbol: String = "₨"): String {
        val formatter = DecimalFormat("#,##0.00")
        return "$symbol ${formatter.format(amount)}"
    }

    fun formatRate(): String {
        val formatter = DecimalFormat("0.00")
        return "${formatter.format(effectiveRatePercent)}%"
    }
}

object PosCalculatorEngine {
    fun calculate(
        amount: Double,
        selectedCategory: PosCategory,
        customPercentage: Double?,
        criteria: CalculationCriteria
    ): PosCalculationResult {
        val safeAmount = if (amount < 0.0) 0.0 else amount
        val effectiveRatePercent = if (customPercentage != null && customPercentage > 0.0) {
            customPercentage
        } else {
            selectedCategory.defaultRatePercent
        }
        val isCustom = customPercentage != null && customPercentage > 0.0

        val rateDecimal = effectiveRatePercent / 100.0

        val posAmount: Double
        val receivedAmount: Double
        val totalCharges: Double

        when (criteria) {
            CalculationCriteria.DEDUCT_FROM_AMOUNT -> {
                // Formula from Sheet:
                // B14 = B4 (POS Amount = Base Amount)
                // B19 = B4 - (B4 * E8) (Received = Base - (Base * Rate))
                // B24 = B14 - B19 (Charges = B4 * Rate)
                posAmount = safeAmount
                receivedAmount = safeAmount - (safeAmount * rateDecimal)
                totalCharges = posAmount - receivedAmount
            }
            CalculationCriteria.ADD_TO_AMOUNT -> {
                // Formula from Sheet:
                // B14 = B4 / (1 - E8) (POS Amount = Base / (1 - Rate))
                // B19 = B4 (Received = Base)
                // B24 = B14 - B19
                if (rateDecimal >= 1.0) {
                    posAmount = safeAmount * 2.0
                    receivedAmount = safeAmount
                    totalCharges = posAmount - receivedAmount
                } else {
                    posAmount = if (safeAmount > 0.0) safeAmount / (1.0 - rateDecimal) else 0.0
                    receivedAmount = safeAmount
                    totalCharges = posAmount - receivedAmount
                }
            }
        }

        return PosCalculationResult(
            baseAmount = safeAmount,
            effectiveRatePercent = effectiveRatePercent,
            isCustomPercentage = isCustom,
            criteria = criteria,
            categoryName = selectedCategory.name,
            posMachineAmount = posAmount,
            amountReceived = receivedAmount,
            totalCharges = totalCharges
        )
    }
}
