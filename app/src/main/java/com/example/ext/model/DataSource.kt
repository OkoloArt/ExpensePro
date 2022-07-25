package com.example.ext.model

import com.example.ext.R
import com.github.mikephil.charting.data.PieEntry

class DataSource {

    fun loadTitle(): List<BottomModal> {
        return listOf(
            BottomModal(R.drawable.groceries_one, "Groceries"),
            BottomModal(R.drawable.snack, "Snacks"),
            BottomModal(R.drawable.cafes, "Cafes & R..."),
            BottomModal(R.drawable.coffee, "Coffee"),
            BottomModal(R.drawable.drinks, "Drinks"),
            BottomModal(R.drawable.beauty, "Beauty"),
            BottomModal(R.drawable.clothing, "Clothing"),
            BottomModal(R.drawable.accessories, "Accessories"),
            BottomModal(R.drawable.gift, "Gifts"),
            BottomModal(R.drawable.film, "Movies"),
            BottomModal(R.drawable.house, "Home"),
            BottomModal(R.drawable.tech, "Tech"),
            BottomModal(R.drawable.subscription, "Subscription"),
            BottomModal(R.drawable.taxi_one, "Taxi"),
            BottomModal(R.drawable.charity, "Charity"),
            BottomModal(R.drawable.books, "Education"),
            BottomModal(R.drawable.drug, "Health"),
            BottomModal(R.drawable.gardening, "Gardening"),
            BottomModal(R.drawable.pet, "Pets"),
            BottomModal(R.drawable.electricity, "Electricity"),
        )
    }

    fun loadEmptyPieData():ArrayList<PieEntry>{
        return arrayListOf(
            PieEntry(0.2f,"Travel"),
            PieEntry(0.2f,"Groceries"),
            PieEntry(0.2f,"Shopping"),
            PieEntry(0.2f,"Entertainment"),
            PieEntry(0.2f,"Bills"),
            PieEntry(0.2f,"Food & Drinks")
        )
    }

}