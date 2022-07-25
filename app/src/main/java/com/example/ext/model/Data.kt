package com.example.ext.model

import androidx.annotation.DrawableRes

data class BottomModal(
    @DrawableRes val categoryImage: Int,
    val categoryName: String
)

data class StatisticsModel(
    val categoryImage: Int,
    val categoryTitle: String,
    val totalSpends: String,
    val totalSpendAmount: String
)
