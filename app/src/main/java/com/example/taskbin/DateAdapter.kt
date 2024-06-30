package com.example.taskbin

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aminography.primecalendar.persian.PersianCalendar
import java.text.SimpleDateFormat
import java.util.Locale

class DateAdapter(
    private val context: Context,
    private val dates: List<PersianCalendar>,
    private val onDateSelected: (PersianCalendar) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dates[position]
        holder.bind(date)

        if (position == selectedPosition) {
            val drawable = GradientDrawable()
            drawable.setColor(Color.parseColor("#F86E3F"))
            drawable.cornerRadius = 16f
            holder.itemView.background = drawable
            holder.tvDate.setTextColor(Color.WHITE)
            holder.tvDayOfWeek.setTextColor(Color.WHITE)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.tvDate.setTextColor(Color.BLACK)
            holder.tvDayOfWeek.setTextColor(Color.BLACK)
            holder.itemView.background = null
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(position)

            onDateSelected(date) // فراخوانی تابع برای تاریخ انتخاب شده
        }
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)

        fun bind(date: PersianCalendar) {
            tvDate.text = date.dayOfMonth.toString()

            val sdf = SimpleDateFormat("EEEE", Locale("fa"))
            val dayOfWeek = sdf.format(date.timeInMillis)
            tvDayOfWeek.text = dayOfWeek
        }
    }
}
