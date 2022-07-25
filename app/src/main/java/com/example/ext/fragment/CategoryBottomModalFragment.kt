package com.example.ext.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ext.adapter.CategoryModalAdapter
import com.example.ext.databinding.FragmentCategoryBottomModalBinding
import com.example.ext.model.DataSource
import com.example.ext.viewmodel.ExpenseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryBottomModalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryBottomModalFragment : BottomSheetDialogFragment() {

    private val expenseViewModel by sharedViewModel<ExpenseViewModel>()

    private var _binding: FragmentCategoryBottomModalBinding? = null
    private val binding get() = _binding!!

    private val categoryList = DataSource().loadTitle()
    private lateinit var adapter: CategoryModalAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBottomModalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryModalAdapter(categoryList) {
            expenseViewModel.setSubCategoryName(it.categoryName)
            expenseViewModel.setSubCategoryImg(it.categoryImage)
            dismiss()
        }
        binding.categoryListRecyclerview.layoutManager =
            GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
        binding.categoryListRecyclerview.adapter = adapter
    }

    companion object {
        const val TAG = "Modal Sheet"
    }
}