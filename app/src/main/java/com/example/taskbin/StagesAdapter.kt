package com.example.taskbin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.StagesEntity

class StagesAdapter(
    private var stages: List<StagesEntity>,
    private val onStageChecked: (StagesEntity, Boolean) -> Unit
) : RecyclerView.Adapter<StagesAdapter.StageViewHolder>() {

    inner class StageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stageName: TextView = itemView.findViewById(R.id.tvStageName)
        val stageCheckBox: CheckBox = itemView.findViewById(R.id.cbStages)

        fun bind(stage: StagesEntity) {
            stageName.text = stage.sName
            stageCheckBox.isChecked = stage.sCheck

            stageCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onStageChecked(stage, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stages, parent, false)
        return StageViewHolder(view)
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {
        holder.bind(stages[position])
    }

    override fun getItemCount(): Int = stages.size

    fun updateStages(newStages: List<StagesEntity>) {
        stages = newStages
        notifyDataSetChanged()
    }

    fun getStages(): List<StagesEntity> {
        return stages
    }
}
