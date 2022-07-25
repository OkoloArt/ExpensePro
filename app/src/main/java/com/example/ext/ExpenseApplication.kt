package com.example.ext

import android.app.Application
import com.example.ext.database.ExpenseRoomDatabase
import com.example.ext.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin


class ExpenseApplication : Application(),KoinComponent {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: ExpenseRoomDatabase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {androidContext(this@ExpenseApplication)
            modules(listOf(applicationModule)) }
    }
}