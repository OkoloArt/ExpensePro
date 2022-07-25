package com.example.ext.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ext.ExpenseApplication
import com.example.ext.ExpenseDatabaseViewModel
import com.example.ext.R
import com.example.ext.databinding.FragmentAddOrEditExpenseBinding
import com.example.ext.viewmodel.ExpenseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [AddExpenseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddOrEditExpenseBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel by sharedViewModel<ExpenseViewModel>()

    private val expenseDatabaseViewModel by sharedViewModel<ExpenseDatabaseViewModel> {
        parametersOf((activity?.application as ExpenseApplication).database.expenseDao())
    }


    private var categoryName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddOrEditExpenseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpinner()
        expenseViewModel.expenseSubCategoryImg.observe(
            viewLifecycleOwner
        ) { id ->
            binding.expenseSubImage.setImageResource(id)
            binding.expenseSubImage.tag = id
        }
        expenseViewModel.expenseSubCategoryName.observe(
            viewLifecycleOwner
        ) { name ->
            binding.expenseSubTitle.text = name
        }
        binding.apply {
            addOrEditExpenseViewModel = expenseViewModel
            addOrEditExpenseFragment = this@AddExpenseFragment
        }
    }

    private fun currentTime(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return formatter.format(date)
    }

    private fun currentDate(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return formatter.format(date)
    }

    fun showModalBottomSheet() {
        val modalBottomSheet = CategoryBottomModalFragment()
        fragmentManager?.let { modalBottomSheet.show(it, CategoryBottomModalFragment.TAG) }
    }

    private fun isExpenseValid(): Boolean {
        return expenseDatabaseViewModel.isEntryValid(
            categoryName,
            binding.expensePrice.editText?.text.toString(),
            binding.expenseSubTitle.text.toString(),
            binding.expenseSubImage.tag as? Int,
        )
    }

    fun addExpense() {
        if (binding.expenseSubImage.tag == null) {
            if (isExpenseValid()) {
                if (checkBudgetBalanceDifference()) {
                    expenseDatabaseViewModel.newExpenseEntry(
                        categoryName,
                        binding.expensePrice.editText?.text.toString(),
                        binding.expenseNote.editText?.text.toString(),
                        binding.expenseSubTitle.text.toString(),
                        R.drawable.pet,
                        currentDate()
                    )
                    findNavController().navigate(R.id.action_addOrEditExpenseFragment_to_expenseListFragment)
                }
            } else {
                Toast.makeText(requireContext(),
                    "Text field cannot be blank",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            if (isExpenseValid()) {
                if (checkBudgetBalanceDifference()) {
                    expenseDatabaseViewModel.newExpenseEntry(
                        categoryName,
                        binding.expensePrice.editText?.text.toString(),
                        binding.expenseNote.editText?.text.toString(),
                        binding.expenseSubTitle.text.toString(),
                        binding.expenseSubImage.tag as? Int,
                        currentDate()
                    )
                    findNavController().navigate(R.id.action_addOrEditExpenseFragment_to_expenseListFragment)
                }
            } else {
                Toast.makeText(requireContext(),
                    "Text field cannot be blank",
                    Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setUpSpinner() {
        val category = resources.getStringArray(R.array.category)

        val spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, category
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup,
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    //here it is possible to define color for other items by
                    //view.setTextColor(Color.RED)
                }
                return view
            }
        }
        binding.expenseCategorySpinner.adapter = spinnerAdapter
        binding.expenseCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    val value = parent!!.getItemAtPosition(position).toString()

                    if (value == category[0]) {
                        hideKeyboard()
                        (view as TextView).setTextColor(Color.GRAY)
                    } else {
                        categoryName = value
                        (view as TextView).setTextColor(Color.WHITE)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    fun hideKeyboard() {
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,
                0)
        }
    }

    private fun checkBudgetBalanceDifference(): Boolean {
        val newEnteredAmount = binding.expensePrice.editText?.text.toString().toInt()
        val totalSpends = expenseViewModel.totalSpends.plus(newEnteredAmount)
        val budgetAmount = expenseViewModel.budgetAmount
        if (totalSpends > budgetAmount) {
            Toast.makeText(requireContext(),
                "Total expense amount is above planned budget",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}