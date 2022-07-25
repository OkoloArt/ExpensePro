package com.example.ext.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ext.database.Expense
import com.example.ext.databinding.ExpenseListDetailBinding
import java.text.NumberFormat
import java.util.*

class ExpenseListAdapter :
    ListAdapter<Expense, ExpenseListAdapter.ExpenseListViewHolder>(DiffCallback) {

    class ExpenseListViewHolder(private val binding: ExpenseListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val symbol = numberFormat.currency?.symbol

        fun bind(expense: Expense) {
            binding.categoryImage.setImageResource(expense.expenseSubCategoryImg)
            binding.categoryTitle.text = expense.expenseSubCategoryName
            binding.currentDate.text = expense.expenseDate
            binding.totalAmount.text="$symbol${expense.expensePrice.toInt()}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseListViewHolder {
        return ExpenseListViewHolder(
            ExpenseListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem.expenseSubCategoryName == newItem.expenseSubCategoryName
            }
        }
    }
}