package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PosDatabase
import com.example.data.repository.PosRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("POS Calculator", appName)
  }

  @Test
  fun `dropdown values seed defaults on first run and persist user changes across re-initialization`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = PosRepository(context)

    // 1. Initial launch: seeds exactly the 3 default categories
    repo.initializeDefaultsIfNeeded()
    val initialCategories = repo.categories.first()
    assertEquals(3, initialCategories.size)
    assertEquals("POS Charges for Customer", initialCategories[0].name)
    assertEquals(2.50, initialCategories[0].defaultRatePercent, 0.001)
    assertEquals("POS Charges for Other Shopkeepers", initialCategories[1].name)
    assertEquals(3.00, initialCategories[1].defaultRatePercent, 0.001)
    assertEquals("POS Charges on International Cards", initialCategories[2].name)
    assertEquals(4.50, initialCategories[2].defaultRatePercent, 0.001)

    // 2. User modifies a rate
    repo.updateCategory(initialCategories[1].id, "POS Charges for Other Shopkeepers", 3.25)

    // 3. User adds a custom category
    repo.addNewCategory("VIP Customer Discount", 1.80)

    // 4. User deletes a category
    repo.deleteCategory(initialCategories[2].id)

    // Check intermediate state
    val modifiedCategories = repo.categories.first()
    assertEquals(3, modifiedCategories.size)
    assertEquals(3.25, modifiedCategories.first { it.id == initialCategories[1].id }.defaultRatePercent, 0.001)
    assertTrue(modifiedCategories.any { it.name == "VIP Customer Discount" && it.defaultRatePercent == 1.80 })
    assertTrue(modifiedCategories.none { it.id == initialCategories[2].id })

    // 5. App restarted or re-initialized (simulate next app launch)
    repo.initializeDefaultsIfNeeded()

    // 6. User modifications MUST NOT be reset!
    val afterRestartCategories = repo.categories.first()
    assertEquals(3, afterRestartCategories.size)
    assertEquals(3.25, afterRestartCategories.first { it.id == initialCategories[1].id }.defaultRatePercent, 0.001)
    assertTrue(afterRestartCategories.any { it.name == "VIP Customer Discount" && it.defaultRatePercent == 1.80 })
    assertTrue(afterRestartCategories.none { it.id == initialCategories[2].id })

    // 7. Explicit reset to default restores the original 3 defaults
    repo.resetCategoriesToDefault()
    val resetCategories = repo.categories.first()
    assertEquals(3, resetCategories.size)
    assertEquals(2.50, resetCategories[0].defaultRatePercent, 0.001)
    assertEquals(3.00, resetCategories[1].defaultRatePercent, 0.001)
    assertEquals(4.50, resetCategories[2].defaultRatePercent, 0.001)
  }
}
