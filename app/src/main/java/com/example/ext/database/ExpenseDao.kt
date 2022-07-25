package com.example.ext.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ExpenseDao {

    @Query("SELECT * from expense ORDER BY subCategoryName ASC")
    fun getExpenses(): Flow<List<Expense>>

    @Query("SELECT * from expense WHERE id = :id")
    fun getExpense(id: Int): Flow<Expense>

    @Query("SELECT DISTINCT category from expense")
    fun getCategory(): List<String>

    @Query("SELECT COUNT(*) from expense WHERE category = :category")
    fun getTotalSpends(category: String): Int

    @Query("SELECT SUM(price) from expense WHERE category = :category")
    fun getTotalAmountByCategory(category: String): Int

    @Query("SELECT SUM(price) from expense")
    fun getTotalAmount(): Flow<Int>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Query("DELETE FROM expense")
    suspend fun delete()
}