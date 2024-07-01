package com.example.taskbin.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.R

class ActivityAdapter(private var activities: List<ActivityEntity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val activityCheckBox: CheckBox = itemView.findViewById(R.id.cbActivity)
        val cardView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardview2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentActivity = activities[position]
        holder.activityName.text = currentActivity.aName
        holder.activityCheckBox.isChecked = currentActivity.aPin

        // تنظیم رنگ پس‌زمینه
        holder.cardView.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, getBackgroundColor(currentActivity.aCategory))
        )
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun setActivities(activities: List<ActivityEntity>) {
        this.activities = activities
        notifyDataSetChanged()
    }

    // تابع کمکی برای تعیین رنگ پس‌زمینه
    private fun getBackgroundColor(category: String): Int {
        return when (category) {
            "Lesson" -> R.color.light_yellow
            "Health" -> R.color.light_green
            "Fun" -> R.color.light_orange
            "Job" -> R.color.light_blue
            "Etc" -> R.color.etc
            else -> R.color.black // رنگ پیش‌فرض در صورت نیاز
        }
    }
}