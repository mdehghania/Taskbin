package com.example.taskbin.Fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.NotificationReceiver
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ProjectViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import java.util.Calendar
import java.util.TimeZone

class AddProjectFragment : Fragment() {

    private lateinit var stagesContainer: LinearLayout
    private var stageCount = 0

    private lateinit var projectNameInput: EditText
    private lateinit var projectHourInput: EditText
    private lateinit var projectTimeInput: EditText
    private lateinit var projectDateInput: EditText
    private lateinit var projectPinCheckBox: CheckBox
    private var userOwnerId: Int = 0
    private var isCheckBoxPinChecked: Boolean = false
    private var selectedDateMillis: Long? = null

    private val projectViewModel: ProjectViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            (requireActivity().application as MyApplication).activityRepository,
            (requireActivity().application as MyApplication).targetRepository,
            (requireActivity().application as MyApplication).projectRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_project, container, false)
        stagesContainer = view.findViewById(R.id.stagesContainer)
        val addStageButton = view.findViewById<ImageButton>(R.id.addStages4)

        projectNameInput = view.findViewById(R.id.projectNameInput)
        projectHourInput = view.findViewById(R.id.projectHoureInput)
        projectTimeInput = view.findViewById(R.id.projectHourInput)
        projectDateInput = view.findViewById(R.id.projectDayInput)
        projectPinCheckBox = view.findViewById(R.id.projectPin)

        isCheckBoxPinChecked = projectPinCheckBox.isChecked
        projectPinCheckBox.setOnCheckedChangeListener { _, isChecked ->
            isCheckBoxPinChecked = isChecked
        }

        projectHourInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showDatePickerDialog()
            }
            true
        }

        projectTimeInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (projectDateInput.text.isNotEmpty()) {
                    showTimePickerDialog()
                }else{
                    Toast.makeText(requireContext(), "لطفاً ابتدا روز مورد نظر را وارد نمایید", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        projectDateInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (projectHourInput.text.isNotEmpty()) {
                    showDatePickerDialogForDateInput()
                } else {
                    Toast.makeText(requireContext(), "لطفاً ابتدا زمان مورد نیاز را وارد نمایید", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        addStageButton.setOnClickListener {
            addNewStage(view.context)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        val btnSaveProject = view.findViewById<Button>(R.id.btnSaveProject)
        btnSaveProject.setOnClickListener {
            saveProject()
        }

        return view
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }

                if (selectedDateMillis != 0L) {
                    val selectedDate = Calendar.getInstance().apply { timeInMillis =
                        selectedDateMillis!!
                    }
                    val currentDate = Calendar.getInstance()
                    if (selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                        selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR) &&
                        selectedTime.before(currentDate)
                    ) {
                        Toast.makeText(requireContext(), "ساعت انتخاب شده نمی‌تواند قبل از ساعت جاری باشد", Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }
                }

                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                val persianTime = timeString.toPersianDigits()
                projectTimeInput.setText(persianTime)
            },
            currentHour,
            currentMinute,
            true
        )
        applyVazirFontToTimePickerDialog(timePickerDialog)
        timePickerDialog.show()
    }


    private fun showDatePickerDialog() {
        val typefaceNormal = ResourcesCompat.getFont(requireContext(), R.font.vazir)
        val actionTextColor = ContextCompat.getColor(requireContext(), R.color.orange)
        val currentCalendar = PersianCalendar()
        val currentYear = currentCalendar.persianYear
        val currentMonth = currentCalendar.persianMonth
        val currentDay = currentCalendar.persianDay

        PersianDatePickerDialog(requireContext())
            .setPositiveButtonString("باشه")
            .setNegativeButton("بیخیال")
            .setTodayButton("امروز")
            .setTodayButtonVisible(true)
            .setMinYear(currentYear)
            .setMaxYear(currentYear + 50)
            .setInitDate(currentYear, currentMonth, currentDay)
            .setActionTextColor(actionTextColor)
            .setTypeFace(typefaceNormal)
            .setPickerBackgroundDrawable(R.drawable.dialog_rounded_corners)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                    val currentDate = PersianCalendar()
                    val selectedDate = PersianCalendar().apply {
                        setPersianDate(persianPickerDate.persianYear, persianPickerDate.persianMonth, persianPickerDate.persianDay)
                    }

                    if (selectedDate.timeInMillis < currentDate.timeInMillis) {
                        Toast.makeText(requireContext(), "تاریخ انتخاب شده نمی‌تواند قبل از تاریخ جاری باشد", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedDateMillis = persianPickerDate.timestamp
                        updateDateInView(persianPickerDate.timestamp)
                    }
                }

                override fun onDismissed() {}
            }).show()
    }

    private fun showDatePickerDialogForDateInput() {
        val typefaceNormal = ResourcesCompat.getFont(requireContext(), R.font.vazir)
        val actionTextColor = ContextCompat.getColor(requireContext(), R.color.orange)
        val currentCalendar = PersianCalendar()
        val currentYear = currentCalendar.persianYear
        val currentMonth = currentCalendar.persianMonth
        val currentDay = currentCalendar.persianDay

        val minDate = currentCalendar.timeInMillis
        val inputString = projectHourInput.text.toString()
        val numericValue = extractNumericPart(inputString)
        val maxDate = minDate + (numericValue * 24 * 60 * 60 * 1000)

        PersianDatePickerDialog(requireContext())
            .setPositiveButtonString("باشه")
            .setNegativeButton("بیخیال")
            .setTodayButton("امروز")
            .setTodayButtonVisible(true)
            .setMinYear(currentYear)
            .setMaxYear(currentYear + 50)
            .setInitDate(currentYear, currentMonth, currentDay)
            .setActionTextColor(actionTextColor)
            .setTypeFace(typefaceNormal)
            .setPickerBackgroundDrawable(R.drawable.dialog_rounded_corners)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                    val selectedDate = PersianCalendar().apply {
                        setPersianDate(persianPickerDate.persianYear, persianPickerDate.persianMonth, persianPickerDate.persianDay)
                    }

                    var selectedDateMillis = selectedDate.timeInMillis / (24 * 60 * 60 * 1000)
                    val minDateMillis = minDate / (24 * 60 * 60 * 1000)
                    val maxDateMillis = maxDate / (24 * 60 * 60 * 1000)

                    if (selectedDateMillis !in minDateMillis..maxDateMillis) {
                        Toast.makeText(requireContext(), "تاریخ انتخاب شده خارج از محدوده مجاز است", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedDateMillis = persianPickerDate.timestamp
                        val formattedDate = "${persianPickerDate.persianYear}/${persianPickerDate.persianMonth}/${persianPickerDate.persianDay}".toPersianDigits()
                        projectDateInput.setText(formattedDate)
                    }
                }

                override fun onDismissed() {}
            }).show()
    }

    private fun extractNumericPart(input: String): Long {
        val numericPart = input.filter { it.isDigit() }
        return numericPart.toLongOrNull() ?: 0L
    }

    private fun applyVazirFontToTimePickerDialog(timePickerDialog: TimePickerDialog) {
        try {
            val field = TimePickerDialog::class.java.getDeclaredField("timePicker")
            field.isAccessible = true
            val timePicker = field.get(timePickerDialog) as TimePicker

            val vazirFont = Typeface.createFromAsset(requireContext().assets, "font/vazirfdwol.ttf")

            for (i in 0 until timePicker.childCount) {
                val view = timePicker.getChildAt(i)
                if (view is EditText) {
                    view.typeface = vazirFont
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun String.toPersianDigits(): String {
        val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val result = StringBuilder()
        for (char in this) {
            if (char.isDigit()) {
                result.append(persianDigits[char.toString().toInt()])
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }



    private fun updateDateInView(timestamp: Long) {
        val currentDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        val diffInMillis = selectedDate.timeInMillis - currentDate.timeInMillis
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        projectHourInput.setText(if (diffInDays < 1) "کمتر از یک روز" else "$diffInDays روز")
    }

    private fun addNewStage(context: Context) {
        stageCount++

        val newStageLayout = RelativeLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10.dpToPx()
            }
        }

        val newEditText = EditText(context).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                230.dpToPx(),
                50.dpToPx()
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            background = resources.getDrawable(R.drawable.inputs, null)
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            textDirection = View.TEXT_DIRECTION_RTL
            setTextColor(resources.getColor(R.color.black, null))
            typeface = resources.getFont(R.font.vazirfdwol)
            setPadding(5.dpToPx(), 0, 5.dpToPx(), 0)
        }

        val newImageButton = ImageButton(context).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                topMargin = 2.dpToPx()
            }
            background = resources.getDrawable(R.drawable.stages, null)
            setOnClickListener {
                stagesContainer.removeView(newStageLayout)
            }
        }

        newStageLayout.addView(newEditText)
        newStageLayout.addView(newImageButton)
        stagesContainer.addView(newStageLayout)
    }



    private fun saveProject() {
        val projectName = projectNameInput.text.toString()
        val projectHour = projectHourInput.text.toString()
        val projectTime = projectTimeInput.text.toString()
        val projectDate = projectDateInput.text.toString()
        val pin = isCheckBoxPinChecked

        if (projectName.isEmpty() || projectHour.isEmpty() ) {
            Toast.makeText(requireContext(), "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
            return
        }

        if (userOwnerId == 0) {
            Toast.makeText(requireContext(), "شناسه کاربر نامعتبر است", Toast.LENGTH_SHORT).show()
            return
        }

        val project = ProjectEntity(
            pName = projectName,
            pHour = projectHour,
            pTime = projectTime,
            pDate = projectDate,
            pPin = pin,
            userOwnerId = userOwnerId,
            completed = false,
        )

        val stages = mutableListOf<StagesEntity>()
        for (i in 0 until stagesContainer.childCount) {
            val stageLayout = stagesContainer.getChildAt(i) as RelativeLayout
            val editText = stageLayout.getChildAt(0) as EditText
            val stageName = editText.text.toString()
            if (stageName.isNotEmpty()) {
                stages.add(StagesEntity(sName = stageName, sCheck = false, projectOwnerId = 0))
            }
        }

        projectViewModel.insertProjectWithStages(project, stages)
        Toast.makeText(requireContext(), "پروژه با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()

        if (projectDate.isNotEmpty() && projectTime.isNotEmpty()) {
            val persianDate = PersianCalendar()
            val parts = projectDate.split("/")
            persianDate.setPersianDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            scheduleNotification(persianDate.timeInMillis, projectTime, projectName)
        }

        activity?.onBackPressed()
    }

    private fun scheduleNotification(date: Long, time: String, projectName: String) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        val timeParts = time.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("projectId", 0)
            putExtra("project_name", projectName)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}