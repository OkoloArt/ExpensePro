package com.example.ext.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ext.databinding.BottomModalDetailBinding
import com.example.ext.model.BottomModal
import com.example.ext.viewmodel.ExpenseViewModel

class CategoryModalAdapter(
    private val dataSet: List<BottomModal>,
    private val onItemClicked: (BottomModal) -> Unit
) : RecyclerView.Adapter<CategoryModalAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: BottomModalDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bottomModal: BottomModal) {
            binding.categoryImage.setImageResource(bottomModal.categoryImage)
            binding.categoryName.text = bottomModal.categoryName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            BottomModalDetailBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       val currentCategory = dataSet[position]
        holder.itemView.setOnClickListener {
            onItemClicked(currentCategory)
        }
        holder.bind(currentCategory)
    }

    override fun getItemCount() = dataSet.size
}