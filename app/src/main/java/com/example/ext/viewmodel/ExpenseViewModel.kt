package com.example.ext.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ext.model.StatisticsModel

class ExpenseViewModel : ViewModel() {

    private val statisticsList = mutableListOf<StatisticsModel>()

    private var _expenseSubCategoryName = MutableLiveData<String>()
    val expenseSubCategoryName: LiveData<String> = _expenseSubCategoryName

    private var _expenseSubCategoryImg = MutableLiveData<Int>()
    val expenseSubCategoryImg: LiveData<Int> = _expenseSubCategoryImg

    private var _budgetAmount: Int = 0
    val budgetAmount get() = _budgetAmount

    private var _totalSpends: Int = 0
    val totalSpends get() = _totalSpends

    private val _statisticsModel = MutableLiveData<MutableList<StatisticsModel>>()
    val statisticsModel: LiveData<MutableList<StatisticsModel>> = _statisticsModel

    // You should call this to update you liveData
    fun addStatistics(
       statisticsModel: StatisticsModel
    ) {
        statisticsList.add(statisticsModel)
        _statisticsModel.postValue(statisticsList)
    }

    fun clearStatistics(
    ) {
        statisticsList.clear()
        _statisticsModel.postValue(statisticsList)
    }

    fun setBudgetAmount(amount: Int) {
        _budgetAmount = amount
    }

    fun setTotalSpends(spends: Int) {
        _totalSpends = spends
    }

    fun setSubCategoryName(subName: String) {
        _expenseSubCategoryName.value = subName
    }

    fun setSubCategoryImg(subImg: Int) {
        _expenseSubCategoryImg.value = subImg
    }

}