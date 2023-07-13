package com.example.savingapp

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.savingapp.Database.DatabaseApp
import com.example.savingapp.Database.RegisterDao
import com.example.savingapp.databinding.ActivityStatisticsBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StatisticsActivity : AppCompatActivity(){
     private var binding: ActivityStatisticsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarStatisticsActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Records"

        binding?.toolbarStatisticsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        val recordDao = (application as DatabaseApp).db.RegisterDao()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MMM")
        val current = LocalDateTime.now().format(formatter)


        val current_splitted_date = current.split("-")
        //binding?.tvTitle?.text = "Total expended for ${current_splitted_date[1]} ${current_splitted_date[0]}:"

        binding?.BtnDate?.setOnClickListener {
            //val selectedDate = selectMontYear(recordDao)
            var dateSelected: Boolean = showMonthYearPicker(recordDao)
        }

    }

    private fun showMonthYearPicker(recordDao: RegisterDao): Boolean {
        // Display the dialog, all the statistics code is on there!
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val dialog = MonthYearPickerDialog(
            context = this@StatisticsActivity, recordDao, binding = binding, lifecycleScope = lifecycleScope,
                                            initialYear = currentYear, initialMonth = currentMonth)
        dialog.show()
        val window: Window = dialog.window!!
        // Set dialog width to a fixed ration of screen width
        window.setLayout((0.8 * resources.displayMetrics.widthPixels).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        return dialog.dateSelected
    }


 }



