package com.example.ext

import androidx.lifecycle.*
import com.example.ext.database.Expense
import com.example.ext.database.ExpenseDao
import kotlinx.coroutines.launch

class ExpenseDatabaseViewModel(private val expenseDao: ExpenseDao) : ViewModel() {

    // Cache all expenses from the database using LiveData.
    val allExpense: LiveData<List<Expense>> = expenseDao.getExpenses().asLiveData()

    val totalAmount: LiveData<Int> = expenseDao.getTotalAmount().asLiveData()

    fun getCat(): List<String> {
        return expenseDao.getCategory()
    }

    fun getTotalSpends(category: String): Int {
        return expenseDao.getTotalSpends(category)
    }

    fun getTotalAmountByCategory(category: String): Int {
        return expenseDao.getTotalAmountByCategory(category)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insert(expense)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteExpense() {
        viewModelScope.launch {
            expenseDao.delete()
        }
    }

    /**
     * Inserts the new Item into database.
     */
    fun newExpenseEntry(
        expenseCategory: String,
        expensePrice: String,
        expenseNote: String,
        expenseSubCategoryName: String,
        expenseSubCategoryImg: Int?,
        expenseDate: String
    ) {
        val addExpense = getNewExpenseEntry(
            expenseCategory,
            expensePrice,
            expenseNote,
            expenseSubCategoryName,
            expenseSubCategoryImg!!,
            expenseDate
        )
        insertExpense(addExpense)
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(
        expenseCategory: String,
        expensePrice: String,
        expenseSubCategoryName: String,
        expenseSubCategoryImg: Int?
    ): Boolean {
        if (expenseCategory.isBlank() || expensePrice.isBlank() || expenseSubCategoryName.isBlank() || expenseSubCategoryImg == 0) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Expense] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewExpenseEntry(
        expenseCategory: String,
        expensePrice: String,
        expenseNote: String,
        expenseSubCategoryName: String,
        expenseSubCategoryImg: Int,
        expenseDate: String
    ): Expense {
        return Expense(
            expenseCategory = expenseCategory,
            expensePrice = expensePrice.toDouble(),
            expenseNote = expenseNote,
            expenseSubCategoryName = expenseSubCategoryName,
            expenseSubCategoryImg = expenseSubCategoryImg,
            expenseDate = expenseDate
        )
    }
}