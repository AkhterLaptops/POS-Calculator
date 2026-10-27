package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "pos_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val baseAmount: Double,
    val categoryName: String,
    val ratePercent: Double,
    val criteriaName: String,
    val posMachineAmount: Double,
    val amountReceived: Double,
    val totalCharges: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "pos_category_rates")
data class CategoryRateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val ratePercent: Double,
    val iconType: String,
    val displayOrder: Int
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM pos_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM pos_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("DELETE FROM pos_transactions")
    suspend fun clearAllTransactions()

    @Query("SELECT COUNT(*) FROM pos_transactions")
    suspend fun getTransactionCount(): Int
}

@Dao
interface CategoryRateDao {
    @Query("SELECT * FROM pos_category_rates ORDER BY displayOrder ASC")
    fun getAllRates(): Flow<List<CategoryRateEntity>>

    @Query("SELECT * FROM pos_category_rates ORDER BY displayOrder ASC")
    suspend fun getAllRatesSync(): List<CategoryRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: CategoryRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<CategoryRateEntity>)

    @Query("UPDATE pos_category_rates SET ratePercent = :ratePercent WHERE id = :id")
    suspend fun updateRate(id: String, ratePercent: Double)

    @Query("UPDATE pos_category_rates SET name = :name, ratePercent = :ratePercent WHERE id = :id")
    suspend fun updateCategory(id: String, name: String, ratePercent: Double)

    @Query("DELETE FROM pos_category_rates WHERE id = :id")
    suspend fun deleteRate(id: String)
}

@Database(
    entities = [TransactionEntity::class, CategoryRateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PosDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryRateDao(): CategoryRateDao

    companion object {
        @Volatile
        private var INSTANCE: PosDatabase? = null

        fun getDatabase(context: Context): PosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PosDatabase::class.java,
                    "pos_calculator_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
