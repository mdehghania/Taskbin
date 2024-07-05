package com.example.taskbin.View


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.R


class ActivityAdapter(
    var activities: MutableList<ActivityEntity>,
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (ActivityEntity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    private var isBinding: Boolean = false

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val activityCheckBox: CheckBox = itemView.findViewById(R.id.tvActivity)
        private val cardView: CardView = itemView.findViewById(R.id.cardview2)

        init {
            activityCheckBox.setOnCheckedChangeListener(null)
            activityCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val activity = activities[position]
                    activity.completed = isChecked
                    onCheckboxClicked(activity.activityId, isChecked)
                    cardView.alpha = if (isChecked) 0.5f else 1f
                    if (!isBinding && isChecked) {
                        Toast.makeText(itemView.context, "هورا موفق شدی!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val activity = activities[position]
                    onItemLongClickListener(activity)
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

        fun bind(activity: ActivityEntity) {
            isBinding = true
            activityName.text = activity.aName
            activityCheckBox.isChecked = activity.completed
            cardView.alpha = if (activity.completed) 0.5f else 1f

            cardView.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, getBackgroundColor(activity.aCategory))
            )
        }

        private fun animateScale(view: View, scale: Float) {
            view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
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
        val sharedPreferences =
            context.getSharedPreferences("CheckBoxPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("checkbox_$id", isChecked)
        editor.apply()
    }

    private fun getCheckBoxState(context: Context, id: Int): Boolean {
        val sharedPreferences =
            context.getSharedPreferences("CheckBoxPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("checkbox_$id", false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(itemView)
    }



    override fun getItemCount(): Int {
        return activities.size
    }

    fun updateActivities(newActivities: List<ActivityEntity>) {
        this.activities = newActivities.toMutableList()
        notifyDataSetChanged()
    }

    fun updateActivityWithHandler(activities: List<ActivityEntity>) {
        this.activities = activities.toMutableList()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getActivityAtPosition(position: Int): ActivityEntity {
        return activities[position]
    }

    fun removeActivityAtPosition(position: Int) {
        activities.removeAt(position)
        notifyItemRemoved(position)
    }
}
