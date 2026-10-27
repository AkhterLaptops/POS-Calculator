package com.example

import com.example.data.model.CalculationCriteria
import com.example.data.model.DEFAULT_POS_CATEGORIES
import com.example.data.model.PosCalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testDeductFromAmount_CustomerRate() {
    val category = DEFAULT_POS_CATEGORIES.first { it.id == "pos_customer" } // 2.50%
    val result = PosCalculatorEngine.calculate(
      amount = 1000.0,
      selectedCategory = category,
      customPercentage = null,
      criteria = CalculationCriteria.DEDUCT_FROM_AMOUNT
    )

    // POS = 1000.0, Received = 975.0, Charges = 25.0
    assertEquals(1000.0, result.posMachineAmount, 0.001)
    assertEquals(975.0, result.amountReceived, 0.001)
    assertEquals(25.0, result.totalCharges, 0.001)
  }

  @Test
  fun testAddToAmount_CustomerRate() {
    val category = DEFAULT_POS_CATEGORIES.first { it.id == "pos_customer" } // 2.50%
    val result = PosCalculatorEngine.calculate(
      amount = 1000.0,
      selectedCategory = category,
      customPercentage = null,
      criteria = CalculationCriteria.ADD_TO_AMOUNT
    )

    val expectedPos = 1000.0 / (1.0 - 0.025)
    assertEquals(expectedPos, result.posMachineAmount, 0.001)
    assertEquals(1000.0, result.amountReceived, 0.001)
    assertEquals(expectedPos - 1000.0, result.totalCharges, 0.001)
  }

  @Test
  fun testCustomPercentage_OverridesCategoryRate() {
    val category = DEFAULT_POS_CATEGORIES.first { it.id == "pos_customer" }
    val customRate = 3.5
    val result = PosCalculatorEngine.calculate(
      amount = 2000.0,
      selectedCategory = category,
      customPercentage = customRate,
      criteria = CalculationCriteria.DEDUCT_FROM_AMOUNT
    )

    assertEquals(3.5, result.effectiveRatePercent, 0.001)
    assertEquals(2000.0, result.posMachineAmount, 0.001)
    assertEquals(2000.0 - (2000.0 * 0.035), result.amountReceived, 0.001)
    assertEquals(2000.0 * 0.035, result.totalCharges, 0.001)
  }

  @Test
  fun testAllCategories_MatchUserTable() {
    assertEquals(3, DEFAULT_POS_CATEGORIES.size)
    val map = DEFAULT_POS_CATEGORIES.associate { it.name to it.defaultRatePercent }
    assertEquals(2.50, map["POS Charges for Customer"] ?: 0.0, 0.001)
    assertEquals(3.00, map["POS Charges for Other Shopkeepers"] ?: 0.0, 0.001)
    assertEquals(4.50, map["POS Charges on International Cards"] ?: 0.0, 0.001)
  }

  @Test
  fun testAppVersion() {
    assertEquals("v2.2", BuildConfig.VERSION_NAME)
  }
}
