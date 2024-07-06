package com.example.taskbin.View

import ActivityAdapter
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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ActivityViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import com.example.taskbin.isSameDay
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ActivityListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private val activityViewModel: ActivityViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            (requireActivity().application as MyApplication).activityRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewActivities)
        adapter = ActivityAdapter(mutableListOf(), { activityId, isChecked ->
            activityViewModel.updateCompletion(activityId, isChecked)
            sortAndNotifyAdapter()
        }, { activity ->
            if (!activity.completed) { // فقط آیتم‌هایی که تکمیل نشده‌اند قابل ویرایش هستند
                showEditDialog(activity)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(SpaceItemDecoration(20, RecyclerView.VERTICAL))

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        val selectedDateMillis =
            arguments?.getLong("selectedDate") ?: PersianCalendar().timeInMillis
        val selectedDate = PersianCalendar().apply { timeInMillis = selectedDateMillis }

        activityViewModel.getActivitiesByUserOwnerId(userOwnerId)
            .observe(viewLifecycleOwner) { activities ->
                activities?.let {
                    val filteredActivities = it.filter { activity ->
                        val activityDate = PersianCalendar().apply { timeInMillis = activity.aDate }
                        activityDate.isSameDay(selectedDate)
                    }
                    adapter.updateActivities(filteredActivities)
                }
            }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val activity = adapter.getActivityAtPosition(position)

                val title = TextView(requireContext()).apply {
                    text = "حذف فعالیت"
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.RIGHT
                    setPadding(0, 20, 20, 0)
                }

                val message = TextView(requireContext()).apply {
                    text = "می خواهی حذف کنی؟"
                    textSize = 16f
                    gravity = Gravity.RIGHT
                    setPadding(0, 20, 20, 0)
                }

                MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                    .setCustomTitle(title)
                    .setView(message)
                    .setPositiveButton("حذف") { dialog, _ ->
                        activityViewModel.delete(activity)
                        dialog.dismiss()
                    }
                    .setNegativeButton("لغو") { dialog, _ ->
                        adapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }
                    .show()
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

                val radii = floatArrayOf(
                    cornerRadius, cornerRadius, // Top left corner
                    cornerRadius, cornerRadius, // Top right corner
                    cornerRadius, cornerRadius, // Bottom right corner
                    cornerRadius, cornerRadius  // Bottom left corner
                )

                val path = android.graphics.Path().apply {
                    addRoundRect(
                        itemView.left.toFloat(), // Left
                        itemView.top.toFloat(), // Top
                        itemView.left + dX, // Right (including swipe distance)
                        itemView.top + itemHeight, // Bottom (same as top + item height)
                        radii,
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
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun sortAndNotifyAdapter() {
        adapter.updateActivityWithHandler(adapter.activities)
    }

    private fun showEditDialog(activity: ActivityEntity) {
        val dialogView = layoutInflater.inflate(R.layout.layout_dialog_edit_activity, null)
        val etTargetName = dialogView.findViewById<EditText>(R.id.activityNameInput)
        val etTargetDescription = dialogView.findViewById<EditText>(R.id.activityDesInput)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val radioButtonLesson = dialogView.findViewById<RadioButton>(R.id.radioButtonLesson)
        val radioButtonHealth = dialogView.findViewById<RadioButton>(R.id.radioButtonHealth)
        val radioButtonFun = dialogView.findViewById<RadioButton>(R.id.radioButtonFun)
        val radioButtonJob = dialogView.findViewById<RadioButton>(R.id.radioButtonJob)
        val radioButtonEtc = dialogView.findViewById<RadioButton>(R.id.radioButtonEtc)
        val etTimeInput = dialogView.findViewById<EditText>(R.id.activityTimeInput)
        val etHourInput = dialogView.findViewById<EditText>(R.id.activityHoureInput)
        val checkBoxPin = dialogView.findViewById<CheckBox>(R.id.checkButtonPin)
        val btnSaveUpdateTarget = dialogView.findViewById<Button>(R.id.btnSaveUpdateTarget)
        val btnCancelUpdateTarget = dialogView.findViewById<Button>(R.id.btnCancleUpdateTarget)

        // Set current data into views
        etTargetName.setText(activity.aName)
        etTargetDescription.setText(activity.aDescription)
        checkBoxPin.isChecked = activity.aPin

        when (activity.aCategory) {
            "Lesson" -> radioButtonLesson.isChecked = true
            "Health" -> radioButtonHealth.isChecked = true
            "Fun" -> radioButtonFun.isChecked = true
            "Job" -> radioButtonJob.isChecked = true
            "Etc" -> radioButtonEtc.isChecked = true
        }

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnSaveUpdateTarget.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButtonText = when (selectedRadioButtonId) {
                R.id.radioButtonLesson -> "Lesson"
                R.id.radioButtonHealth -> "Health"
                R.id.radioButtonFun -> "Fun"
                R.id.radioButtonJob -> "Job"
                R.id.radioButtonEtc -> "Etc"
                else -> null
            }

            activity.aName = etTargetName.text.toString()
            activity.aDescription = etTargetDescription.text.toString()
            activity.aCategory = selectedRadioButtonText ?: ""
            activity.aPin = checkBoxPin.isChecked

            activityViewModel.update(activity)
            dialogBuilder.dismiss()
        }

        btnCancelUpdateTarget.setOnClickListener {
            dialogBuilder.dismiss()
        }

        // Set dialog width
        dialogBuilder.setOnShowListener {
            val window = dialogBuilder.window
            window?.setLayout(350.dpToPx(requireContext()), ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialogBuilder.show()
    }

    // Convert dp to px
    fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }
}
