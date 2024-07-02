
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
    private var targets: List<TargetEntity>,
    private val onCheckboxClicked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TargetAdapter.TargetViewHolder>() {

    inner class TargetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val cbActivity: CheckBox = itemView.findViewById(R.id.cbActivity)
        private val showdate: TextView = itemView.findViewById(R.id.showdate)

        init {
            cbActivity.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val target = targets[position]
                    onCheckboxClicked(target.targetId, isChecked)
                }
            }
        }

        fun bind(target: TargetEntity) {
            tvActivityName.text = target.tName
            cbActivity.isChecked = target.completed
            if (target.completed) {
                cardView.alpha = 0.5f // Example: Show disabled state with transparency
            } else {
                cardView.alpha = 1f // Return to default state
            }

            // Convert and display date in Persian (Shamsi) format
            val persianDate = PersianDateConverter.convertToPersianDate(target.timestamp)
            showdate.text = persianDate

            cbActivity.setOnCheckedChangeListener(null) // Prevent unwanted triggering during binding
            cbActivity.isChecked = target.completed
            cbActivity.setOnCheckedChangeListener { _, isChecked ->
                target.completed = isChecked
                onCheckboxClicked(target.targetId, isChecked)
                cardView.alpha = if (isChecked) 0.5f else 1f
            }
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

    fun setTargets(targets: List<TargetEntity>) {
        this.targets = targets
        notifyDataSetChanged()
    }
}
