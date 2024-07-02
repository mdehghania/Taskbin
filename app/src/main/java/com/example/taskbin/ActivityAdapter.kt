package com.example.taskbin.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.R

class ActivityAdapter(private var activities: List<ActivityEntity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val activityCheckBox: CheckBox = itemView.findViewById(R.id.tvActivity)
        private val cardView: CardView = itemView.findViewById(R.id.cardview2)

        fun bind(activity: ActivityEntity, isChecked: Boolean, context: Context) {
            activityName.text = activity.aName
            activityCheckBox.isChecked = isChecked

            // Set background color based on category
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, getBackgroundColor(activity.aCategory))
            )

            // Set card view alpha based on checkbox state
            if (isChecked) {
                cardView.alpha = 0.5f // Disabled state
            } else {
                cardView.alpha = 1f // Default state
            }

            // Save checkbox state in SharedPreferences
            activityCheckBox.setOnCheckedChangeListener(null) // Prevent unwanted triggering during binding
            activityCheckBox.isChecked = isChecked
            activityCheckBox.setOnCheckedChangeListener { _, isChecked ->
                saveCheckBoxState(context, activity.activityId, isChecked)
                cardView.alpha = if (isChecked) 0.5f else 1f
            }
        }

        private fun getBackgroundColor(category: String): Int {
            return when (category) {
                "Lesson" -> R.color.light_yellow
                "Health" -> R.color.light_green
                "Fun" -> R.color.light_orange
                "Job" -> R.color.light_blue
                "Etc" -> R.color.etc
                else -> R.color.black // Default color if needed
            }
        }

        private fun saveCheckBoxState(context: Context, id: Int, isChecked: Boolean) {
            val sharedPreferences = context.getSharedPreferences("CheckBoxPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("checkbox_$id", isChecked)
            editor.apply()
        }

        fun getCheckBoxState(context: Context, id: Int): Boolean {
            val sharedPreferences = context.getSharedPreferences("CheckBoxPreferences", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("checkbox_$id", false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentActivity = activities[position]
        val isChecked = holder.getCheckBoxState(holder.itemView.context, currentActivity.activityId)
        holder.bind(currentActivity, isChecked, holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun setActivities(activities: List<ActivityEntity>) {
        this.activities = activities
        notifyDataSetChanged()
    }
}
