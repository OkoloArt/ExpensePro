package com.example.ext.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ext.ExpenseApplication
import com.example.ext.ExpenseDatabaseViewModel
import com.example.ext.R
import com.example.ext.adapter.StatisticsAdapter
import com.example.ext.databinding.FragmentStatisticsBinding
import com.example.ext.model.DataSource
import com.example.ext.model.StatisticsModel
import com.example.ext.viewmodel.ExpenseViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val expenseDatabaseViewModel by sharedViewModel<ExpenseDatabaseViewModel> {
        parametersOf((activity?.application as ExpenseApplication).database.expenseDao())
    }
    private val expenseViewModel by sharedViewModel<ExpenseViewModel>()

    private lateinit var pieChart: PieChart
    private lateinit var adapter: StatisticsAdapter
    private val statisticsList = mutableSetOf<StatisticsModel>()
    private var totalSpends = ""
    private var totalSpendAmount = ""
    private var totalSum = 0
    private var entriesData = mutableListOf<PieEntry>()
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val symbol = numberFormat.currency?.symbol

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = binding.pieChart
        entriesData.clear()
        loadPieChartData()
        loadCategoryDetail()
        setUpPieChart()

        adapter = StatisticsAdapter()
        binding.statisticsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.statisticsRecyclerView.adapter = adapter
        expenseViewModel.statisticsModel.observe(viewLifecycleOwner) { statistics ->
            statistics?.let {
                adapter.notifyDataSetChanged()
                adapter.submitList(statistics)
            }
        }
        halfHeight()
    }

    private fun setUpPieChart() {
        pieChart.apply {
            setDrawRoundedSlices(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            centerText = "${symbol}$totalSum"
            setCenterTextColor(Color.WHITE)
            setHoleColor(Color.TRANSPARENT)
            setCenterTextSize(30f)
            setCenterTextTypeface(Typeface.SANS_SERIF)
            holeRadius = 65f
            isRotationEnabled = true
            setDrawEntryLabels(false)
            description.isEnabled = false
        }

        val legend = pieChart.legend
        legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            form = Legend.LegendForm.SQUARE
            formSize = 13f
            yOffset = 10f
            isWordWrapEnabled = true
            textSize = 16f
            textColor = Color.WHITE
            isEnabled = true
            xEntrySpace = 15f
            formToTextSpace = 9f
        }
    }

    private fun loadPieChartData() {
        val dummyEntries = DataSource().loadEmptyPieData()

        val colors = arrayListOf<Int>()
        for (i in ColorTemplate.MATERIAL_COLORS) {
            colors.add(i)
        }
        for (i in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(i)
        }

        val dataset: PieDataSet = if (entriesData.isEmpty()) {
            PieDataSet(dummyEntries, "")
        } else {
            PieDataSet(entriesData.distinctBy { it.label }, "")
        }
        dataset.colors = colors
        dataset.sliceSpace = 6f
        val data = PieData(dataset)
        data.setDrawValues(false)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        //   pieChart.notifyDataSetChanged()
        pieChart.invalidate()

        Handler(Looper.getMainLooper()).post {
            // Your Code
            pieChart.animateY(1500, Easing.EaseInOutQuad)
        }

    }

    private fun halfHeight() {
        val displayMetrics = requireActivity().windowManager.defaultDisplay
        val height = displayMetrics.height / 2.5
        binding.pieChart.layoutParams.height = height.toInt()
    }

    private fun clearList(){
       expenseViewModel.clearStatistics()
    }
    private fun loadCategoryDetail() {

        CoroutineScope(Dispatchers.Default).launch {
            clearList()
            val categoryName = expenseDatabaseViewModel.getCat().toList()
            for (i in categoryName.indices) {
                totalSum += expenseDatabaseViewModel.getTotalAmountByCategory(categoryName[i])
                totalSpends =
                    expenseDatabaseViewModel.getTotalSpends(categoryName[i]).toString()
                totalSpendAmount =
                    expenseDatabaseViewModel.getTotalAmountByCategory(categoryName[i]).toString()
                entriesData.add(PieEntry(totalSpendAmount.toFloat(), categoryName[i]))
                pieChart.notifyDataSetChanged()
                loadPieChartData()
                setUpPieChart()
                when (categoryName[i]) {
                    "Travel" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.travel,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount)
                        )
                    }
                    "Food & Drinks" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.food_drinks,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount
                        )
                        )
                    }
                    "Entertainment" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.entertainment,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount
                        )
                        )
                    }
                    "Groceries" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.groceries,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount
                        )
                        )
                    }
                    "Bills" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.bill,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount
                        )
                        )
                    }
                    "Shopping" -> {
                        expenseViewModel.addStatistics(StatisticsModel(R.drawable.shopping,
                            categoryName[i],
                            totalSpends,
                            totalSpendAmount
                        )
                        )
                    }
                }
            }
        }

    }
}
