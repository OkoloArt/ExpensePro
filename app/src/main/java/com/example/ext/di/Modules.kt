package com.example.ext.di

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.room.Room
import com.example.ext.ExpenseDatabaseViewModel
import com.example.ext.R
import com.example.ext.database.ExpenseRoomDatabase
import com.example.ext.datastore.UserManager
import com.example.ext.viewmodel.ExpenseViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val applicationModule = module(override = true) {

    single { UserManager(androidContext()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            ExpenseRoomDatabase::class.java,
            "expense_database"
        ).build()
    }

    single<Animation>(named("SplashBottom")) {
        AnimationUtils.loadAnimation(
            androidContext(),
            R.anim.splash_bottom_animation
        )
    }

    single<Animation>(named("SplashTop")) {
        AnimationUtils.loadAnimation(
            androidContext(),
            R.anim.splash_top_animation
        )
    }

    viewModel {
        ExpenseViewModel()
    }

    viewModel {
        ExpenseDatabaseViewModel(get())
    }
}


