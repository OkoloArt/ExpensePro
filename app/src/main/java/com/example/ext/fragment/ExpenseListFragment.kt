package com.example.ext.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ext.ExpenseApplication
import com.example.ext.ExpenseDatabaseViewModel
import com.example.ext.R
import com.example.ext.adapter.ExpenseListAdapter
import com.example.ext.databinding.FragmentExpenseListBinding
import com.example.ext.datastore.UserManager
import com.example.ext.viewmodel.ExpenseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess


/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpenseListFragment : Fragment() {

//    private val expenseDatabaseViewModel: ExpenseDatabaseViewModel by activityViewModels {
//        ExpenseDatabaseViewModelFactory((activity?.application as ExpenseApplication).database.expenseDao())
//    }

    private val expenseDatabaseViewModel by sharedViewModel<ExpenseDatabaseViewModel> {
        parametersOf((activity?.application as ExpenseApplication).database.expenseDao())
    }

    private val expenseViewModel by sharedViewModel<ExpenseViewModel>()

    private val userManager by inject<UserManager>()
    private var budgetAmount = 0
    private var budgetBalance = 0
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val symbol = numberFormat.currency?.symbol

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpenseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExpenseListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.finish()
                    exitProcess(0)
                }
            })
        adapter = ExpenseListAdapter()

        binding.apply {
            expenseListFragment = this@ExpenseListFragment

            expenseListRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            expenseListRecyclerview.adapter = adapter

            expenseDatabaseViewModel.allExpense.observe(viewLifecycleOwner) { expense ->
                expense?.let {
                    adapter.submitList(it)
                }
            }

            expenseDatabaseViewModel.totalAmount.observe(viewLifecycleOwner) { amount ->
                amount?.let {
                    budgetBalance = it
                    lifecycleScope.launch {
                        userManager.storeUserBalance(budgetBalance, requireContext())
                    }
                }
            }
        }
        observeData()

    }

    fun showStatistics() {
        findNavController().navigate(R.id.action_expenseListFragment_to_statisticsFragment)
    }

    fun addExpense() {
        if (budgetAmount == 0) {
            Toast.makeText(requireContext(), "Please set a budget", Toast.LENGTH_SHORT)
                .show()
        } else if (budgetBalance >= checkBudgetBalanceDifference() && budgetBalance < budgetAmount) {
            Toast.makeText(requireContext(),
                "You're above three quarter of your budget",
                Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                findNavController().navigate(R.id.action_expenseListFragment_to_addOrEditExpenseFragment)
            }, 1000)
        } else if (budgetBalance >= budgetAmount) {
            Toast.makeText(requireContext(),
                "You've reached your set budget. Mind increasing your budget. Do not that SAPA is real",
                Toast.LENGTH_SHORT).show()
        } else if (budgetAmount != 0) {
            findNavController().navigate(R.id.action_expenseListFragment_to_addOrEditExpenseFragment)
        }
    }

    fun showDialog() {
        val inflater = requireActivity().layoutInflater;
        val view = inflater.inflate(R.layout.dialog_layout, null)
        val inputLayout = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
        MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(view)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                // Respond to negative button press
                val input = inputLayout.editText?.text.toString()
                saveBudget(input)
            }
            .show()
    }

    fun deleteDialog() {
        MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(resources.getString(R.string.previous_month_budget))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                // Respond to positive button press
                delete()
            }
            .show()
    }

    private fun observeData() {

        // Updates amount
        // every time user age changes it will be observed by userBudgetFlow
        // here it refers to the value returned from the userBudgetFlow function
        // of UserManager class
        userManager.preferenceBudgetFlow.asLiveData().observe(viewLifecycleOwner) {
            budgetAmount = it
            binding.budget.text = "$symbol${budgetAmount}"
            expenseViewModel.setBudgetAmount(budgetAmount)
        }

        // Updates balance
        // every time balance changes it will be observed by userBalanceFlow
        // here it refers to the value returned from the userBalanceFlow function
        // of UserManager class
        userManager.preferenceBalanceFlow.asLiveData().observe(viewLifecycleOwner) {
            budgetBalance = it
            binding.balance.text = "$symbol${budgetBalance}"
            expenseViewModel.setTotalSpends(budgetBalance)
        }
    }

    private fun delete() {
        when (currentDate()) {
            "01" -> {
                // Launch a coroutine and write the layout setting in the preference Datastore
                expenseDatabaseViewModel.deleteExpense()
                lifecycleScope.launch {
                    userManager.storeUserBalance(0, requireContext())
                    userManager.storeUserBudget(0, requireContext())
                }
                Toast.makeText(
                    requireContext(),
                    "All previous Expense has been deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Expense can only be deleted at the start of a new month",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun currentDate(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd")
        return formatter.format(date)
    }

    private fun checkBudgetBalanceDifference(): Double {
        return 0.75 * budgetAmount
    }

    private fun saveBudget(budget: String) {
        if (budget.isNotBlank()) {
            budgetAmount = budget.toInt()
            // Launch a coroutine and write the layout setting in the preference Datastore
            lifecycleScope.launch {
                userManager.storeUserBudget(
                    budgetAmount,
                    requireContext()
                )
            }
        }
    }
}

