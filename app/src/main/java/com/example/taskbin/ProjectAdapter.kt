package com.example.taskbin.View

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ProjectViewModel

class ProjectAdapter(
    var projects: MutableList<ProjectEntity>,
    private val onCheckboxClicked: (Int, Boolean) -> Unit,
    private val onItemLongClickListener: (ProjectEntity) -> Unit,
    private val projectViewModel: ProjectViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var recyclerView: RecyclerView? = null

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViewP)
        private val tvProjectName: TextView = itemView.findViewById(R.id.tvProjectName)
        private val cbProject: CheckBox = itemView.findViewById(R.id.cbProjet)
        private val timeNeed: TextView = itemView.findViewById(R.id.timeNeed)
        private var pinIcon: ImageView? = null
        private val pinStub: ViewStub = itemView.findViewById(R.id.pin_stub)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private var completedStagesCount: Int = 0
        private var totalStagesCount: Int = 0
        private var isBinding: Boolean = false

        init {
            cbProject.setOnCheckedChangeListener(null)
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
                    updateProgressBar()
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
            timeNeed.text = project.pHour
            cardView.alpha = if (project.completed) 0.5f else 1f
            isBinding = false

            if (project.pPin) {
                if (pinIcon == null) {
                    pinIcon = pinStub.inflate() as ImageView
                }
                pinIcon?.visibility = View.VISIBLE
            } else {
                pinIcon?.visibility = View.GONE
            }

            projectViewModel.getStagesByProjectOwnerId(project.projectId).observe(lifecycleOwner, Observer { stages ->
                if (stages != null) {
                    completedStagesCount = stages.count { it.sCheck }
                    totalStagesCount = stages.size
                    val progress = if (totalStagesCount > 0) (completedStagesCount * 100 / totalStagesCount) else 0

                    if (!project.completed && progressBar.progress != progress) {
                        animateProgressBar(progressBar, progress)
                    } else {
                        progressBar.progress = progress
                    }
                    checkAndUpdateCompletion(project, progress, itemView)
                }
            })
        }

        private fun updateProgressBar() {
            val progress = if (totalStagesCount > 0) (completedStagesCount * 100 / totalStagesCount) else 0

            if (!projects[adapterPosition].completed) {
                animateProgressBar(progressBar, progress)
            } else {
                progressBar.progress = progress
            }
        }

        fun animateProgressBar(progressBar: ProgressBar, toProgress: Int) {
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    super.applyTransformation(interpolatedTime, t)
                    progressBar.progress = (interpolatedTime * toProgress).toInt()
                }
            }
            animation.duration = 1000
            progressBar.startAnimation(animation)
        }

        private fun animateScale(view: View, scale: Float) {
            view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
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
        this.projects = projects.toMutableList()
        notifyDataSetChanged()
    }

    fun updateProjectsWithHandler(projects: List<ProjectEntity>) {
        this.projects = projects.toMutableList()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getProjectAtPosition(position: Int): ProjectEntity {
        return projects[position]
    }

    fun removeProjectAtPosition(position: Int) {
        projects.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateProjectCompletion(projectId: Int, isCompleted: Boolean, progress: Int) {
        val project = projects.find { it.projectId == projectId }
        project?.let {
            it.completed = isCompleted
            val position = projects.indexOf(it)
            notifyItemChanged(position)


            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? ProjectViewHolder
            viewHolder?.let { holder ->
                holder.animateProgressBar(holder.progressBar, progress)
            }
        }
    }

    private fun checkAndUpdateCompletion(project: ProjectEntity, progress: Int, itemView: View) {
        if (progress == 100 && !project.completed) {
            project.completed = true
            projectViewModel.updateCompletion(project.projectId, true)
            Toast.makeText(itemView.context, "پروژه ${project.pName} تکمیل شد!", Toast.LENGTH_SHORT).show()
        }
    }
}

