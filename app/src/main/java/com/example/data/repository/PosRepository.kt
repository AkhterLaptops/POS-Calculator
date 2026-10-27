package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.CategoryRateEntity
import com.example.data.local.PosDatabase
import com.example.data.local.TransactionEntity
import com.example.data.model.DEFAULT_POS_CATEGORIES
import com.example.data.model.PosCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PosRepository(private val context: Context) {
    private val db = PosDatabase.getDatabase(context)
    private val transactionDao = db.transactionDao()
    private val rateDao = db.categoryRateDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("pos_prefs", Context.MODE_PRIVATE)

    val transactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    val categories: Flow<List<PosCategory>> = rateDao.getAllRates().map { entities ->
        if (entities.isEmpty()) {
            DEFAULT_POS_CATEGORIES
        } else {
            entities.map {
                PosCategory(
                    id = it.id,
                    name = it.name,
                    defaultRatePercent = it.ratePercent,
                    iconType = it.iconType
                )
            }
        }
    }

    companion object {
        const val PREF_KEY_INITIALIZED_PRESETS = "has_initialized_v2_2_presets"
    }

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val alreadyInitialized = prefs.getBoolean(PREF_KEY_INITIALIZED_PRESETS, false)
        val existing = rateDao.getAllRatesSync()

        // If presets were already initialized and table is not empty, preserve all user modifications
        if (alreadyInitialized && existing.isNotEmpty()) {
            return@withContext
        }

        // Check if there is old legacy data from pre-v2.2 versions that must be cleaned
        val hasLegacyData = existing.any {
            it.id.startsWith("card_") ||
            it.id.startsWith("qr_") ||
            it.id == "pos_corporate" ||
            it.id == "pos_amex" ||
            it.id == "pos_advance_tax"
        }

        if (!alreadyInitialized || existing.isEmpty() || hasLegacyData) {
            if (existing.isEmpty() || hasLegacyData) {
                existing.forEach { rateDao.deleteRate(it.id) }
                val entities = DEFAULT_POS_CATEGORIES.mapIndexed { index, cat ->
                    CategoryRateEntity(
                        id = cat.id,
                        name = cat.name,
                        ratePercent = cat.defaultRatePercent,
                        iconType = cat.iconType,
                        displayOrder = index
                    )
                }
                rateDao.insertAll(entities)
            }
            prefs.edit().putBoolean(PREF_KEY_INITIALIZED_PRESETS, true).apply()
        }
    }

    suspend fun updateCategory(id: String, newName: String, newRatePercent: Double) = withContext(Dispatchers.IO) {
        rateDao.updateCategory(id, newName.trim(), newRatePercent)
    }

    suspend fun addNewCategory(name: String, ratePercent: Double) = withContext(Dispatchers.IO) {
        val count = rateDao.getAllRatesSync().size
        val newId = "custom_${System.currentTimeMillis()}"
        val entity = CategoryRateEntity(
            id = newId,
            name = name.trim(),
            ratePercent = ratePercent,
            iconType = "card",
            displayOrder = count
        )
        rateDao.insertRate(entity)
    }

    suspend fun deleteCategory(id: String) = withContext(Dispatchers.IO) {
        rateDao.deleteRate(id)
    }

    suspend fun resetCategoriesToDefault() = withContext(Dispatchers.IO) {
        val existing = rateDao.getAllRatesSync()
        existing.forEach { rateDao.deleteRate(it.id) }
        val entities = DEFAULT_POS_CATEGORIES.mapIndexed { index, cat ->
            CategoryRateEntity(
                id = cat.id,
                name = cat.name,
                ratePercent = cat.defaultRatePercent,
                iconType = cat.iconType,
                displayOrder = index
            )
        }
        rateDao.insertAll(entities)
        prefs.edit().putBoolean(PREF_KEY_INITIALIZED_PRESETS, true).apply()
    }

    suspend fun saveTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun clearAllTransactions() = withContext(Dispatchers.IO) {
        transactionDao.clearAllTransactions()
    }

    fun getCurrencySymbol(): String {
        return prefs.getString("currency_symbol", "₨") ?: "₨"
    }

    fun setCurrencySymbol(symbol: String) {
        prefs.edit().putString("currency_symbol", symbol).apply()
    }
}
