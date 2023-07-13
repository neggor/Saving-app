package com.example.savingapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.savingapp.Database.DatabaseApp
import com.example.savingapp.Database.RegisterDao
import com.example.savingapp.Database.RegisterEntity
import com.example.savingapp.databinding.ActivityMainBinding
import com.example.savingapp.databinding.SelectCategoryBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var selectedCategory = "Not Selected"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val registerDao = (application as DatabaseApp).db.RegisterDao()

        binding?.BtnCategory?.setOnClickListener {
            selectCategoriesDialog()
        }
        supportActionBar?.hide()

        updateText( price = binding?.ETPrice?.text.toString(),
                    category = selectedCategory)

       // binding?.TVSummary?.visibility = View.INVISIBLE

        binding?.ETPrice?.addTextChangedListener {
            updateText(it.toString(), selectedCategory)
        }

        binding?.BtnAddEntry?.setOnClickListener {
            addEntryToDatabase(registerDao)

        }
        binding?.BtnHistory?.setOnClickListener {
            val intent = Intent(this, HistoryActivity:: class.java)
            startActivity(intent)
        }
    }



    private fun updateText(price: String, category: String){
        var my_price = price
        if (my_price == ""){
            my_price = "0"
        }
        binding?.TVSummary?.text = "Add $my_price € to $category"
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    private fun selectCategoriesDialog(){
        val customDialog = Dialog(this)
        val dialogBinding = SelectCategoryBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)

        fun dismiss(){
            customDialog.dismiss()
            updateText( price = binding?.ETPrice?.text.toString(),
                category = selectedCategory)
            binding?.TVSummary?.visibility = View.VISIBLE
        }

        dialogBinding.Groceries.setOnClickListener {
            selectedCategory = dialogBinding.Groceries.text.toString()
            Thread.sleep(150)
            dismiss()
        }
        dialogBinding.Rent.setOnClickListener {
            selectedCategory = dialogBinding.Rent.text.toString()
            Thread.sleep(150)
            dismiss()        }
        dialogBinding.Insurance.setOnClickListener {
            selectedCategory = dialogBinding.Insurance.text.toString()
            Thread.sleep(150)
            dismiss()        }
        dialogBinding.Gym.setOnClickListener {
            selectedCategory = dialogBinding.Gym.text.toString()
            Thread.sleep(150)
            dismiss()        }
        dialogBinding.Books.setOnClickListener {
            selectedCategory = dialogBinding.Books.text.toString()
            Thread.sleep(150)
            dismiss()        }
        dialogBinding.Transport.setOnClickListener {
            selectedCategory = dialogBinding.Transport.text.toString()
            Thread.sleep(100)
            dismiss()        }
        dialogBinding.OrderFood.setOnClickListener {
            selectedCategory = dialogBinding.OrderFood.text.toString()
            Thread.sleep(100)
            dismiss()        }



        customDialog.show()
    }
    private fun  addEntryToDatabase(registerDao: RegisterDao){
        if (binding?.ETPrice?.text.toString() == "" || selectedCategory == "None" ){
            Toast.makeText(this,
                "I need category and price!",
                Toast.LENGTH_LONG).show()
            return
        }
        val c = Calendar.getInstance()
        val dateTime = c.time
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)

        lifecycleScope.launch{
            registerDao.insert(RegisterEntity(
                date = date,
                price = binding?.ETPrice?.text.toString().toFloat(),
                category = selectedCategory))
        }
        Toast.makeText(this,
            "Entry added!",
            Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onRestart() {
        super.onRestart()
        binding?.ETPrice?.text?.clear()
        selectedCategory = "Not Selected"
        updateText( price = binding?.ETPrice?.text.toString(),
            category = selectedCategory)
    }
}