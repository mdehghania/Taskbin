package com.example.taskbin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.TargetEntity

class TargetAdapter(private var targets: List<TargetEntity>) : RecyclerView.Adapter<TargetAdapter.TargetViewHolder>() {

    class TargetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        val cbActivity: CheckBox = itemView.findViewById(R.id.cbActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_target, parent, false)
        return TargetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TargetViewHolder, position: Int) {
        val target = targets[position]
        holder.tvActivityName.text = target.tName
        holder.cbActivity.isChecked = target.completed  // Assuming 'completed' is a boolean in TargetEntity
    }

    override fun getItemCount(): Int {
        return targets.size
    }

    fun setTargets(targets: List<TargetEntity>) {
        this.targets = targets
        notifyDataSetChanged()
    }
}
