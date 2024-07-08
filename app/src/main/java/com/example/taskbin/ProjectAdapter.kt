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
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.R

class ProjectAdapter(
    var projects: MutableList<ProjectEntity>,  // تغییر به MutableList
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (ProjectEntity) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var isBinding: Boolean = false

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViewP)
        private val tvProjectName: TextView = itemView.findViewById(R.id.tvProjectName)
        private val cbProject: CheckBox = itemView.findViewById(R.id.cbProjet)

        init {
            cbProject.setOnCheckedChangeListener(null) // جلوگیری از تریگر ناخواسته در هنگام بایند کردن
            cbProject.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val project = projects[position]
                    project.completed = isChecked
                    onCheckboxClicked(project.projectId, isChecked)
                    cardView.alpha = if (isChecked) 0.5f else 1f
                    if (!isBinding && isChecked) {
                        Toast.makeText(itemView.context, "پروژه تکمیل شد!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val project = projects[position]
                    onItemLongClickListener(project)
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

        fun bind(project: ProjectEntity) {
            isBinding = true
            tvProjectName.text = project.pName
            cbProject.isChecked = project.completed
            cardView.alpha = if (project.completed) 0.5f else 1f
            isBinding = false
        }

        private fun animateScale(view: View, scale: Float) {
            view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    fun updateProjects(projects: List<ProjectEntity>) {
        this.projects = projects.toMutableList()  // تبدیل به MutableList
        notifyDataSetChanged()
    }

    fun updateProjectsWithHandler(projects: List<ProjectEntity>) {
        this.projects = projects.toMutableList()  // تبدیل به MutableList
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getProjectAtPosition(position: Int): ProjectEntity {
        return projects[position]
    }

    fun removeProjectAtPosition(position: Int) {
        projects.removeAt(position)  // حذف آیتم
        notifyItemRemoved(position)
    }
}
