package com.example.taskbin.Fragment


import SpaceItemDecoration
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.View.ProjectAdapter
import com.example.taskbin.ViewModel.ProjectViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProjectListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private val projectViewModel: ProjectViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            null, // چون در اینجا نیاز به ActivityRepository نداریم
            null, // چون در اینجا نیاز به TargetRepository نداریم
            (requireActivity().application as MyApplication).projectRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewProject)

        adapter = ProjectAdapter(mutableListOf(), { projectId, isChecked ->
            projectViewModel.updateCompletion(projectId, isChecked)
            sortAndNotifyAdapter()
        }, { project ->
            if (!project.completed) { // فقط آیتم‌هایی که تکمیل نشده‌اند قابل ویرایش هستند
                showEditDialog(project)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val spaceInPixels = resources.getDimensionPixelSize(R.dimen.item_space)
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceInPixels, RecyclerView.VERTICAL))

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        projectViewModel.getProjectByUserOwnerId(userOwnerId)
            .observe(viewLifecycleOwner) { projects ->
                projects?.let {
                    adapter.updateProjects(it)
                    sortAndNotifyAdapter()
                }
            }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.RIGHT) {
                        val title = TextView(requireContext()).apply {
                            text = "حذف پروژه"
                            textSize = 20f
                            typeface = Typeface.DEFAULT_BOLD
                            gravity = Gravity.RIGHT
                            setPadding(0, 20, 20, 0)
                        }

                        val message = TextView(requireContext()).apply {
                            text = "می‌خواهی حذف کنی؟"
                            textSize = 16f
                            gravity = Gravity.RIGHT
                            setPadding(0, 20, 20, 0)
                        }

                        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                            .setCustomTitle(title)
                            .setView(message)
                            .setNegativeButton("لغو") { dialog, _ ->
                                dialog.dismiss()
                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                            }
                            .setPositiveButton("حذف") { dialog, _ ->
                                val position = viewHolder.adapterPosition
                                val project = adapter.getProjectAtPosition(position)
                                projectViewModel.delete(project)
                                adapter.removeProjectAtPosition(position)
                            }
                            .show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.backgrond))
                    val cornerRadius = 40f
                    val itemHeight = itemView.height.toFloat()

                    val path = android.graphics.Path().apply {
                        addRoundRect(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            itemView.left + dX,
                            itemView.top + itemHeight,
                            floatArrayOf(
                                cornerRadius,
                                0f,
                                0f,
                                cornerRadius,
                                0f,
                                cornerRadius,
                                0f,
                                0f
                            ),
                            android.graphics.Path.Direction.CW
                        )
                    }

                    c.clipPath(path)

                    background.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt(), itemView.bottom
                    )
                    background.draw(c)

                    val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
                    val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                    val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconBottom = iconTop + icon.intrinsicHeight
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        viewHolder?.itemView?.alpha = 0.7f
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    viewHolder.itemView.alpha = 1.0f
                    super.clearView(recyclerView, viewHolder)
                }
            }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun sortAndNotifyAdapter() {
        val sortedProjects = adapter.projects.sortedBy { it.completed }
        adapter.updateProjectsWithHandler(sortedProjects)
    }

    private fun showEditDialog(project: ProjectEntity) {
        val dialogView = layoutInflater.inflate(R.layout.layout_dialog_edit_target, null)
//        val etTargetName = dialogView.findViewById<EditText>(R.id.targetNameInput)
//        val etTargetDescription = dialogView.findViewById<EditText>(R.id.targetDesInput)
//        val btnSaveUpdateTarget = dialogView.findViewById<Button>(R.id.btnSaveUpdateTarget)
        val btnCancleUpdateTarget = dialogView.findViewById<Button>(R.id.btnCancleUpdateTarget)

//        etTargetName.setText(target.tName)
//        etTargetDescription.setText(target.tDesc)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        btnSaveUpdateTarget.setOnClickListener {
//            target.tName = etTargetName.text.toString()
//            target.tDesc = etTargetDescription.text.toString()
//
//            targetViewModel.update(target)
//            sortAndNotifyAdapter() // مرتب‌سازی پس از به‌روزرسانی داده‌ها
//            dialogBuilder.dismiss()
//        }

        btnCancleUpdateTarget.setOnClickListener {
            dialogBuilder.dismiss()
        }
        dialogBuilder.setOnShowListener {
            val window = dialogBuilder.window
            window?.setLayout(350.dpToPx(requireContext()), ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialogBuilder.show()
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
