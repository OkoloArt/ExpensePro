package com.example.ext.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class ExpenseRoomDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}