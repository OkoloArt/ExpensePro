package com.example.ext.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "category")
    val expenseCategory: String,
    @ColumnInfo(name = "price")
    val expensePrice: Double,
    @ColumnInfo(name = "note")
    val expenseNote: String,
    @ColumnInfo(name = "subCategoryName")
    val expenseSubCategoryName: String,
    @ColumnInfo(name = "subCategoryImg")
    val expenseSubCategoryImg: Int,
    @ColumnInfo(name = "expenseDate")
    val expenseDate: String,
)