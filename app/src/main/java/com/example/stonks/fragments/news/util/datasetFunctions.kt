package com.example.stonks.fragments.news.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.stonks.constants.colorChartLine
import com.example.stonks.constants.statisticsShowDays
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

fun initDataset(values: MutableList<Float>, context: Context): LineDataSet {

    val valuesEntries: ArrayList<Entry> = ArrayList()
    for (i in 0.until(values.size))
        valuesEntries.add(Entry(i.toFloat(), values[i]))


    val dataSet = LineDataSet(valuesEntries, "Costs")
    dataSet.axisDependency = YAxis.AxisDependency.LEFT
    dataSet.color = ContextCompat.getColor(context, colorChartLine)
    dataSet.setCircleColor(ContextCompat.getColor(context, colorChartLine))
    dataSet.lineWidth = 4f
    dataSet.valueTextSize = 0f
    dataSet.setDrawCircleHole(false)

    return dataSet
}