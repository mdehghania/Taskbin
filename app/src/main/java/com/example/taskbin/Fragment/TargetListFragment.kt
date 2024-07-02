package com.example.taskbin.View

import SpaceItemDecoration
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.TargetViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TargetListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TargetAdapter
    private val targetViewModel: TargetViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            null,
            (requireActivity().application as MyApplication).targetRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_target_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTarget)
        adapter = TargetAdapter(mutableListOf(), { targetId, isChecked ->
            targetViewModel.updateCompletion(targetId, isChecked)
            sortAndNotifyAdapter()
        }, { target ->
            if (!target.completed) { // فقط آیتم‌هایی که تکمیل نشده‌اند قابل ویرایش هستند
                showEditDialog(target)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val spaceInPixels = resources.getDimensionPixelSize(R.dimen.item_space) // Define item_space in resources
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceInPixels, RecyclerView.VERTICAL))

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        targetViewModel.getTargetsByUserOwnerId(userOwnerId)
            .observe(viewLifecycleOwner) { targets ->
                targets?.let {
                    adapter.updateTargets(it)
                    sortAndNotifyAdapter() // اضافه کردن مرتب‌سازی پس از بارگذاری داده‌ها
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
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete Target")
                            .setMessage("Are you sure you want to delete this target?")
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                            }
                            .setPositiveButton("Delete") { dialog, _ ->
                                val position = viewHolder.adapterPosition
                                val target = adapter.getTargetAtPosition(position)
                                targetViewModel.delete(target)
                                adapter.removeTargetAtPosition(position)
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
                    val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Red))
                    val cornerRadius = 40f
                    val itemHeight = itemView.height.toFloat()

                    val path = android.graphics.Path().apply {
                        addRoundRect(
                            itemView.left.toFloat(), // Left
                            itemView.top.toFloat(), // Top
                            itemView.left + dX, // Right (including swipe distance)
                            itemView.top + itemHeight, // Bottom (same as top + item height)
                            floatArrayOf(
                                cornerRadius, // Top left corner: rounded
                                0f, // Top right corner: 0 radius
                                0f, // Bottom right corner: 0 radius
                                cornerRadius, // Bottom left corner: rounded
                                0f, // Bottom left corner: 0 radius
                                cornerRadius, // Top left corner: rounded
                                0f, // Top right corner: 0 radius
                                0f // Bottom right corner: 0 radius
                            ),
                            android.graphics.Path.Direction.CW
                        )
                    }

// Apply the clip path to limit the drawing area
                    c.clipPath(path)

                    // Draw the background color
                    background.setBounds(
                        itemView.left, itemView.top,
                        itemView.left + dX.toInt(), itemView.bottom
                    )
                    background.draw(c)

                    // Draw the delete icon
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
        val sortedTargets = adapter.targets.sortedBy { it.completed }
        adapter.updateTargetsWithHandler(sortedTargets)
    }

    private fun showEditDialog(target: TargetEntity) {
        val dialogView = layoutInflater.inflate(R.layout.layout_dialog_edit_target, null)
        val etTargetName = dialogView.findViewById<EditText>(R.id.targetNameInput)
        val etTargetDescription = dialogView.findViewById<EditText>(R.id.targetDesInput)

        etTargetName.setText(target.tName)
        etTargetDescription.setText(target.tDesc)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("ویرایش هدف")
            .setView(dialogView)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Update") { dialog, _ ->
                target.tName = etTargetName.text.toString()
                target.tDesc = etTargetDescription.text.toString()

                targetViewModel.update(target)
                sortAndNotifyAdapter() // مرتب‌سازی پس از به‌روزرسانی داده‌ها
            }
            .show()
    }
}
