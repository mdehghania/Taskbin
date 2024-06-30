package com.example.taskbin.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.R

class ActivityAdapter(private var activities: List<ActivityEntity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val activityCheckBox: CheckBox = itemView.findViewById(R.id.cbActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentActivity = activities[position]
        holder.activityName.text = currentActivity.aName
        holder.activityCheckBox.isChecked = currentActivity.aPin
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun setActivities(activities: List<ActivityEntity>) {
        this.activities = activities
        notifyDataSetChanged()
    }
}
