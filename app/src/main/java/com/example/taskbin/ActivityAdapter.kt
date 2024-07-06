import android.content.Context
import android.os.Handler
import android.os.Looper
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

class ActivityAdapter(
    var activities: MutableList<ActivityEntity>,
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (ActivityEntity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    var checkedItemPosition: Int = RecyclerView.NO_POSITION // Change visibility to public

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val activityCheckBox: CheckBox = itemView.findViewById(R.id.tvActivity)
        private val cardView: CardView = itemView.findViewById(R.id.cardview2)

        init {
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

        fun bind(activity: ActivityEntity, isChecked: Boolean, context: Context) {
            activityName.text = activity.aName
            activityCheckBox.isChecked = isChecked
            cardView.alpha = if (isChecked) 0.5f else 1f

            cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, getBackgroundColor(activity.aCategory))
            )

            activityCheckBox.setOnCheckedChangeListener(null)
            activityCheckBox.isChecked = isChecked
            activityCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val activity = activities[position]
                    activity.completed = isChecked
                    onCheckboxClicked(activity.activityId, isChecked)
                    cardView.alpha = if (isChecked) 0.5f else 1f
                    saveCheckBoxState(context, activity.activityId, isChecked)
                }
            }
        }

        private fun getBackgroundColor(category: String): Int {
            return when (category) {
                "Lesson" -> R.color.light_yellow
                "Health" -> R.color.light_green
                "Fun" -> R.color.light_orange
                "Job" -> R.color.light_blue
                "Etc" -> R.color.etc
                else -> R.color.black
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
        val activity = activities[position]
        val context = holder.itemView.context
        val isChecked = holder.getCheckBoxState(context, activity.activityId)
        holder.bind(activity, isChecked, context)
    }

    private fun animateScale(view: View, scale: Float) {
        view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun updateActivities(newActivities: List<ActivityEntity>) {
        this.activities = newActivities.toMutableList()
        sortActivities() // Sort activities when updating the list
        notifyDataSetChanged()
    }

    fun getActivityAtPosition(position: Int): ActivityEntity {
        return activities[position]
    }

    fun updateActivityWithHandler(activities: List<ActivityEntity>) {
        this.activities = activities.toMutableList()
        Handler(Looper.getMainLooper()).post {
            sortActivities() // Sort activities when updating with handler
            notifyDataSetChanged()
        }
    }

    private fun sortActivities() {
        activities.sortBy { it.completed }
    }

    private fun moveItemToEnd(position: Int) {
        val item = activities.removeAt(position)
        activities.add(item)
        notifyItemMoved(position, activities.size - 1)
    }

    private fun moveItemToStart(position: Int) {
        val item = activities.removeAt(position)
        activities.add(0, item)
        notifyItemMoved(position, 0)
    }
}
