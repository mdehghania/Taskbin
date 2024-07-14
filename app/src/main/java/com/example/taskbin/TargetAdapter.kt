package com.example.taskbin.View

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.R
import java.util.Locale

class TargetAdapter(
    var targets: MutableList<TargetEntity>,  // تغییر به MutableList
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (TargetEntity) -> Unit
) : RecyclerView.Adapter<TargetAdapter.TargetViewHolder>() {

    private var isBinding: Boolean = false

    inner class TargetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val cbActivity: CheckBox = itemView.findViewById(R.id.cbActivity)
        private val tvShowDate: TextView = itemView.findViewById(R.id.showdate)

        init {
            cbActivity.setOnCheckedChangeListener(null)
            cbActivity.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val target = targets[position]
                    target.completed = isChecked
                    onCheckboxClicked(target.targetId, isChecked)
                    cardView.alpha = if (isChecked) 0.5f else 1f
                    if (!isBinding && isChecked) {
                        Toast.makeText(itemView.context, "هورا موفق شدی!", Toast.LENGTH_SHORT).show()
                    }
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
            isBinding = true
            tvActivityName.text = target.tName
            cbActivity.isChecked = target.completed
            cardView.alpha = if (target.completed) 0.5f else 1f

            val persianCalendar = PersianCalendar().apply {
                timeInMillis = target.timestamp
            }
            val formattedDate = String.format(
                Locale.getDefault(),
                "%04d/%02d/%02d",
                persianCalendar.year,
                persianCalendar.month + 1,
                persianCalendar.dayOfMonth
            )
            tvShowDate.text = formattedDate
            isBinding = false
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
        this.targets = targets.toMutableList()
        notifyDataSetChanged()
    }

    fun updateTargetsWithHandler(targets: List<TargetEntity>) {
        this.targets = targets.toMutableList()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getTargetAtPosition(position: Int): TargetEntity {
        return targets[position]
    }

    fun removeTargetAtPosition(position: Int) {
        targets.removeAt(position)
        notifyItemRemoved(position)
    }
}
