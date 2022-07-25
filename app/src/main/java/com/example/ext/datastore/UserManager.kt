package com.example.ext.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LAYOUT_PREFERENCES_NAME = "layout_preferences"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)

class UserManager(context: Context) {

    // Store user budget
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeUserBudget(budget: Int, context: Context) {
        context.dataStore.edit {
            it[BUDGET_AMOUNT] = budget
        }
    }

    // Store user budget
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeUserBalance(balance: Int, context: Context) {
        context.dataStore.edit {
            it[BALANCE_AMOUNT] = balance
        }
    }

    val preferenceBudgetFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // On the first run of the app, we will use 0 by default
            preferences[BUDGET_AMOUNT] ?: 0
        }

    val preferenceBalanceFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // On the first run of the app, we will use 0 by default
            preferences[BALANCE_AMOUNT] ?: 0
        }

    companion object{
        private val BUDGET_AMOUNT = intPreferencesKey("budget_amount")
        private val BALANCE_AMOUNT = intPreferencesKey("balance_amount")
    }
}