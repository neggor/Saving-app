package com.example.savingapp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.savingapp.Database.RegisterDao
import com.example.savingapp.Database.RegisterEntity
import com.example.savingapp.databinding.ActivityStatisticsBinding
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MonthYearPickerDialog(
    context: Context,
    val recordDao: RegisterDao,
    val lifecycleScope: LifecycleCoroutineScope,
    var binding: ActivityStatisticsBinding?,
    private val initialYear: Int,
    private val initialMonth: Int,

) : Dialog(context) {
    var dateSelected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_month_year_picker, null)
        setContentView(view)

        val monthPicker = view.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = view.findViewById<NumberPicker>(R.id.yearPicker)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthPicker.value = initialMonth

        yearPicker.minValue = 1900 // Set your desired minimum year
        yearPicker.maxValue = currentYear
        yearPicker.value = initialYear

        view.findViewById<View>(R.id.btnOk).setOnClickListener {
            val selectedMonth = monthPicker.value
            val selectedYear = yearPicker.value
            getSums(recordDao, monthPicker.displayedValues[selectedMonth], selectedYear.toString(), lifecycleScope)
            dateSelected = true
            dismiss()
        }
        //view.findViewById<View>(R.id.btnCancel).setOnClickListener {
         //   dismiss()
        //}
    }

    private fun filterEntry(date: RegisterEntity, month: String, year: String): Boolean {
        return month in date.date && year in date.date

    }
    // This group by and sum is working now, it seems!
    fun getSums(registerDao: RegisterDao, month: String, year: String, lifecycleScope: LifecycleCoroutineScope) {
        binding?.llStatistics?.removeAllViews()
        binding?.tvTitle?.text = "Total expended for ${month} ${year}:"
        //TODO(Solve this mess, it's not working properly. The selected date is not matching the format of the date in the database)

        var month_short = month.substring(0, 3).toLowerCase()
        lifecycleScope.launch {
            registerDao.fetchAllDates().collect() { sumsList ->
                if (sumsList.isNotEmpty()) {

                    val dates = arrayListOf<RegisterEntity>()
                    for (date in sumsList) {
                        Log.d("MYPD", date.toString())
                        Log.d("MYPD", month_short)
                        Log.d("MYPD", year)
                       if (filterEntry(date, month_short, year)) {
                            Log.d("MYPD", month_short)
                            Log.d("MYPD", date.toString())
                            dates.add(date)
                        }
                    }
                    val mySum = dates
                        .groupBy { it.category }
                        .mapValues { entry -> entry.value.sumOf { it.price.toLong() } }
                    //Log.d("Data", mySum.toList().toString())
                    // This is a list of tuples (category, sum). We can iterate over it to display the results
                    var barEntriesList: ArrayList<BarEntry> = ArrayList()
                    var labels: ArrayList<String> = ArrayList()
                    lateinit var barDataSet: BarDataSet
                    lateinit var barData: BarData
                    var barChart = binding?.idBarChart
                    for ((i, category) in mySum.toList().withIndex()) {
                        barEntriesList.add(BarEntry(i.toFloat(), category.second.toFloat()))
                        labels.add(category.first)
                        val tv_dynamic = TextView(context)
                        if (i % 2 == 0){
                            tv_dynamic.setBackgroundColor(Color.parseColor("#000000"))
                            tv_dynamic.setTextColor(Color.parseColor("#FFFFFF"))
                            tv_dynamic.text = "Total for ${category.first} is ${category.second}"
                            binding?.llStatistics?.addView(tv_dynamic)
                        }
                        else{
                            tv_dynamic.setBackgroundColor(Color.parseColor("#FFFFFF"))
                            tv_dynamic.setTextColor(Color.parseColor("#000000"))
                            tv_dynamic.text = "Total for ${category.first} is ${category.second}"
                            binding?.llStatistics?.addView(tv_dynamic)
                        }

                    }
                    barDataSet = BarDataSet(barEntriesList, "Total for ${month} ${year}")
                    barDataSet.setDrawValues(false)
                    barData = BarData(barDataSet)

                    // on below line we are setting data to our bar chart
                    if (barChart != null) {
                        Log.d("Data", "Setting data")
                        barChart.data = barData
                    }

                    // on below line we are setting colors for our bar chart text
                    barDataSet.valueTextColor = Color.BLACK
                    // on below line we are setting text size
                    barDataSet.valueTextSize = 16f

                    // on below line we are enabling description as false
                    if (barChart != null) {
                        barChart.description.isEnabled = false
                    }

                    if (barChart != null) {
                        barChart.getXAxis().setValueFormatter(IndexAxisValueFormatter(labels))
                        barChart.getXAxis().setLabelRotationAngle(-45f);
                        barChart.getXAxis().textSize = 9f
                    }
                    // Avoid the labels to be overlapping
                    if (barChart != null) {
                        barChart.getXAxis().setGranularity(1f);
                    }

                    barChart!!.notifyDataSetChanged();
                    barChart.invalidate();


                }else{
                    Log.d("No data", "Seems there is no data to display")
                }
            }
        }
    }

}

