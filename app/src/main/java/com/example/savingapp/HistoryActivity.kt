package com.example.savingapp

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savingapp.Database.DatabaseAdaptor
import com.example.savingapp.Database.DatabaseApp
import com.example.savingapp.Database.RegisterDao
import com.example.savingapp.Database.RegisterEntity
import com.example.savingapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistoryActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Records"

        binding?.toolbarHistoryActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        val recordDao = (application as DatabaseApp).db.RegisterDao()
        getAllCompleteDates(recordDao)

        binding?.rvHistory?.getChildAt(0)

        binding?.BtnStatistics?.setOnClickListener {
            val intent = Intent(this, StatisticsActivity:: class.java)
            startActivity(intent)
        }
    }


    private fun getAllCompleteDates(registerDao: RegisterDao){
        lifecycleScope.launch{
            registerDao.fetchAllDates().collect{
                    allCompletedDatesList ->
                if(allCompletedDatesList.isNotEmpty()){
                    binding?.IVHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.INVISIBLE

                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    val dates = ArrayList<RegisterEntity>()
                    for(date in allCompletedDatesList){
                        dates.add(date)
                    }

                    val historyAdaptor = DatabaseAdaptor(dates,
                        this@HistoryActivity,
                        layoutInflater,
                        application)

                    binding?.rvHistory?.adapter = historyAdaptor
                }else{
                    binding?.IVHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}