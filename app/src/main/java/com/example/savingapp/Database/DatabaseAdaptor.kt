package com.example.savingapp.Database

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.savingapp.R
import com.example.savingapp.databinding.AreYouSureDialogBinding
import com.example.savingapp.databinding.ItemHistoryBinding
import com.example.savingapp.databinding.SelectCategoryBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DatabaseAdaptor(private val items: ArrayList<RegisterEntity>,
                      private val my_context: Context,
                      private val my_inflater: LayoutInflater,
                      private val application: Application): RecyclerView.Adapter<DatabaseAdaptor.ViewHolder>() {

    class ViewHolder(binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){
        val llHistoryItemMain = binding.llHistoryItemMain
        val tvDate = binding.tVDate
        val tvPrice = binding.tvPrice
        val tvCategory = binding.tvCategory
        val tvPosition = binding.tvPosition
        val btnDelete = binding.BtnDelete


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: RegisterEntity = items.get(position)
        val recordDao = (application as DatabaseApp).db.RegisterDao()

        holder.tvPosition.text = (position + 1).toString()
        holder.tvDate.text = data.date
        holder.tvPrice.text = data.price.toString() + "€"
        holder.tvCategory.text = data.category

        if(position % 2 == 0){
            holder.llHistoryItemMain.setBackgroundColor(Color.parseColor("#000000"))
            holder.tvPosition.setTextColor(Color.parseColor("#FFFFFF"))
            holder.tvDate.setTextColor(Color.parseColor("#FFFFFF"))
            holder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"))
            holder.tvCategory.setTextColor(Color.parseColor("#FFFFFF"))
            holder.btnDelete.setBackgroundDrawable(ContextCompat.getDrawable(my_context, R.drawable.ripple_effect_white_background))
        }else{
            holder.llHistoryItemMain.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.tvDate.setTextColor(Color.parseColor("#000000"))
            holder.tvPosition.setTextColor(Color.parseColor("#000000"))
            holder.tvPrice.setTextColor(Color.parseColor("#000000"))
            holder.tvCategory.setTextColor(Color.parseColor("#000000"))
            holder.btnDelete.setBackgroundDrawable(ContextCompat.getDrawable(my_context, R.drawable.ripple_effect_black_background))
            holder.btnDelete.setTextColor(Color.parseColor("#FFFFFF"))
        }

        holder.btnDelete.setOnClickListener {
            val customDialog = Dialog(my_context)
            val dialogBinding = AreYouSureDialogBinding.inflate(my_inflater)
            customDialog.setContentView(dialogBinding.root)
            customDialog.setCanceledOnTouchOutside(true)

            dialogBinding.BtnNo.setOnClickListener {
                customDialog.dismiss()
            }
            dialogBinding.btnYes.setOnClickListener {
                GlobalScope.launch {
                    recordDao.deleteEntry(data)

                }
                Toast.makeText(my_context, "Entry deleted!", Toast.LENGTH_LONG).show()
                customDialog.dismiss()
            }
            customDialog.show()
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}