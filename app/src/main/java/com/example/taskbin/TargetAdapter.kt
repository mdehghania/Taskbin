package com.example.taskbin.View

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.R

class TargetAdapter(
    var targets: MutableList<TargetEntity>,  // تغییر به MutableList
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (TargetEntity) -> Unit
) : RecyclerView.Adapter<TargetAdapter.TargetViewHolder>() {

    inner class TargetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val cbActivity: CheckBox = itemView.findViewById(R.id.cbActivity)

        init {
            cbActivity.setOnCheckedChangeListener(null) // جلوگیری از تریگر ناخواسته در هنگام بایند کردن
            cbActivity.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val target = targets[position]
                    target.completed = isChecked
                    onCheckboxClicked(target.targetId, isChecked)
                    cardView.alpha = if (isChecked) 0.5f else 1f
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val target = targets[position]
                    onItemLongClickListener(target)
                }
                true
            }

            itemView.setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        animateScale(v, 0.95f)
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        animateScale(v, 1f)
                    }
                }
                false
            }
        }

        fun bind(target: TargetEntity) {
            tvActivityName.text = target.tName
            cbActivity.isChecked = target.completed
            cardView.alpha = if (target.completed) 0.5f else 1f
        }

        private fun animateScale(view: View, scale: Float) {
            view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_target, parent, false)
        return TargetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TargetViewHolder, position: Int) {
        val target = targets[position]
        holder.bind(target)
    }

    override fun getItemCount(): Int {
        return targets.size
    }

    fun updateTargets(targets: List<TargetEntity>) {
        this.targets = targets.toMutableList()  // تبدیل به MutableList
        notifyDataSetChanged()
    }

    fun updateTargetsWithHandler(targets: List<TargetEntity>) {
        this.targets = targets.toMutableList()  // تبدیل به MutableList
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getTargetAtPosition(position: Int): TargetEntity {
        return targets[position]
    }

    fun removeTargetAtPosition(position: Int) {
        targets.removeAt(position)  // حذف آیتم
        notifyItemRemoved(position)
    }
}
