package com.example.ext.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ext.database.Expense
import com.example.ext.databinding.StatisticsModelDetailBinding
import com.example.ext.model.StatisticsModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class StatisticsAdapter: ListAdapter<StatisticsModel,StatisticsAdapter.StatisticsViewHolder>(DiffCallback) {

    class StatisticsViewHolder(private val binding: StatisticsModelDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(statisticsModel: StatisticsModel) {
            binding.categoryTitle.text = statisticsModel.categoryTitle
            binding.totalAmount.text ="$${statisticsModel.totalSpendAmount}"
            when(statisticsModel.totalSpends){
                "1" -> binding.noOfCategorySpends.text = "${statisticsModel.totalSpends} spend"
                else -> binding.noOfCategorySpends.text = "${statisticsModel.totalSpends} spends"
            }
            binding.currentDate.text = getCurrentDate()
            binding.categoryImage.setImageResource(statisticsModel.categoryImage)
        }

        private fun getCurrentDate(): String {
            val date = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            return formatter.format(date)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        return StatisticsViewHolder(
            StatisticsModelDetailBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val currentCategory = getItem(position)
        holder.bind(currentCategory)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StatisticsModel>() {
            override fun areItemsTheSame(oldItem: StatisticsModel, newItem: StatisticsModel): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: StatisticsModel, newItem: StatisticsModel): Boolean {
                return oldItem.categoryImage == newItem.categoryImage
            }
        }
    }

}