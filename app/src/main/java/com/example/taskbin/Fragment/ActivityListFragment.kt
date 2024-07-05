package com.example.taskbin.View



import SpaceItemDecoration
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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

        val spaceInPixels =
            resources.getDimensionPixelSize(R.dimen.item_space) // Define item_space in resources
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceInPixels, RecyclerView.VERTICAL))

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        activityViewModel.getActivitiesByUserOwnerId(userOwnerId)
            .observe(viewLifecycleOwner) { targets ->
                targets?.let {
                    adapter.updateActivities(it)
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
                                val target = adapter.getActivityAtPosition(position)
                                activityViewModel.delete(target)
                                adapter.removeActivityAtPosition(position)
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
                    val background =
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.Red))
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
            }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun PersianCalendar.isSameDay(other: PersianCalendar): Boolean {
        return this.year == other.year && this.month == other.month && this.dayOfMonth == other.dayOfMonth
    }

    private fun sortAndNotifyAdapter() {
        val sortedTargets = adapter.activities.sortedBy { it.completed }
        adapter.updateActivityWithHandler(sortedTargets)
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
        val btnCancleUpdateTarget = dialogView.findViewById<Button>(R.id.btnCancleUpdateTarget)

        // تنظیم اطلاعات فعلی در ویوها
        etTargetName.setText(activity.aName)
        etTargetDescription.setText(activity.aDescription)
        checkBoxPin.isChecked = activity.aPin // تنظیم وضعیت پین

        // انتخاب رادیو دکمه مناسب
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
            sortAndNotifyAdapter() // مرتب‌سازی پس از به‌روزرسانی داده‌ها
            dialogBuilder.dismiss()
        }

        btnCancleUpdateTarget.setOnClickListener {
            dialogBuilder.dismiss()
        }

        // تنظیم عرض دیالوگ
        dialogBuilder.setOnShowListener {
            val window = dialogBuilder.window
            window?.setLayout(350.dpToPx(requireContext()), ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialogBuilder.show()
    }

    // تبدیل dp به px
    fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }

}