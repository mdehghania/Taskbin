package com.example.taskbin.View

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ReminderBroadcastReceiver
import com.example.taskbin.ViewModel.ActivityViewModel
import com.example.taskbin.ViewModel.SharedViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import java.util.Calendar

class AddActivityFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var btnBackAddActivity: ImageView
    private lateinit var btnSaveActivity: Button
    private lateinit var activityNameInput: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonLesson: RadioButton
    private lateinit var radioButtonHealth: RadioButton
    private lateinit var radioButtonFun: RadioButton
    private lateinit var radioButtonJob: RadioButton
    private lateinit var radioButtonEtc: RadioButton
    private lateinit var activityDesInput: EditText
    private lateinit var activityTimeInput: EditText
    private lateinit var checkButtonPin: CheckBox
    private lateinit var activityHoureInput: EditText
    private val activityViewModel: ActivityViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            (requireActivity().application as MyApplication).activityRepository
        )
    }

    private var selectedRadioButtonText: String? = null
    private var isCheckBoxPinChecked: Boolean = false
    private var userOwnerId: Int = 0
    private lateinit var selectedDate: PersianCalendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_activity, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        btnBackAddActivity = view.findViewById(R.id.btnBackAddActivity)
        btnSaveActivity = view.findViewById(R.id.btnSaveActivity)
        activityNameInput = view.findViewById(R.id.activityNameInput)
        activityDesInput = view.findViewById(R.id.activityDesInput)
        radioGroup = view.findViewById(R.id.radioGroup)
        radioButtonLesson = view.findViewById(R.id.radioButtonLesson)
        radioButtonHealth = view.findViewById(R.id.radioButtonHealth)
        radioButtonFun = view.findViewById(R.id.radioButtonFun)
        radioButtonJob = view.findViewById(R.id.radioButtonJob)
        radioButtonEtc = view.findViewById(R.id.radioButtonEtc)
        activityTimeInput = view.findViewById(R.id.activityTimeInput)
        checkButtonPin = view.findViewById(R.id.checkButtonPin)
        activityHoureInput = view.findViewById(R.id.activityHoureInput)

        isCheckBoxPinChecked = checkButtonPin.isChecked
        checkButtonPin.setOnCheckedChangeListener { _, isChecked ->
            isCheckBoxPinChecked = isChecked
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateSelectedRadioButtonText(checkedId)
        }

        activityTimeInput.inputType = InputType.TYPE_NULL
        activityTimeInput.setOnClickListener {
            showTimeDurationPickerDialog()
        }

        activityHoureInput.inputType = InputType.TYPE_NULL
        activityHoureInput.setOnClickListener {
            showTimePickerDialog(activityHoureInput)
        }

        btnBackAddActivity.setOnClickListener {
            activity?.onBackPressed()
        }

        val sharedPreferences1 = requireContext().getSharedPreferences("SelectedDatePrefs", Context.MODE_PRIVATE)
        val selectedDateMillis = sharedPreferences1.getLong("selectedDate", PersianCalendar().timeInMillis)
        selectedDate = PersianCalendar().apply { timeInMillis = selectedDateMillis }

        sharedViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            selectedDate = date
        }

        btnSaveActivity.setOnClickListener {
            if (validateInput()) {
                saveActivity()

                sharedViewModel.setSelectedDate(selectedDate)
                activity?.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "لطفا همه ی فیلد ها را پر کنید.", Toast.LENGTH_SHORT).show()
            }
        }


        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        // Get selected date from arguments
        selectedDate = arguments?.getSerializable("selectedDate") as? PersianCalendar ?: PersianCalendar()

        btnBackAddActivity.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun updateSelectedRadioButtonText(checkedId: Int) {
        selectedRadioButtonText = when (checkedId) {
            R.id.radioButtonLesson -> "Lesson"
            R.id.radioButtonHealth -> "Health"
            R.id.radioButtonFun -> "Fun"
            R.id.radioButtonJob -> "Job"
            R.id.radioButtonEtc -> "Etc"
            else -> null
        }
    }

    private fun showTimePickerDialog(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate.timeInMillis
        }

        val isToday = calendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            if (isToday && (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute))) {
                Toast.makeText(requireContext(), "زمان انتخاب شده نمی تواند قبل از زمان جاری باشد", Toast.LENGTH_SHORT).show()
                showTimePickerDialog(targetEditText) // نمایش دوباره دیالوگ
            } else {
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                targetEditText.setText(convertToPersian(formattedTime))
            }
        }, currentHour, currentMinute, true)

        if (isToday) {
            timePickerDialog.updateTime(currentHour, currentMinute)
        }

        timePickerDialog.show()
    }



    private fun showTimeDurationPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_time_duration_picker, null)
        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.minutePicker)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        // تنظیم محدوده مقادیر
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.wrapSelectorWheel = true

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.wrapSelectorWheel = true
        minutePicker.setFormatter { i -> String.format("%02d", i) }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        okButton.setOnClickListener {
            val selectedHour = hourPicker.value
            val selectedMinute = minutePicker.value
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            activityTimeInput.setText(selectedTime)
            dialog.dismiss()
        }
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(250.dpToPx(requireContext()), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun convertToPersian(input: String): String {
        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        val builder = StringBuilder()
        for (char in input) {
            if (char.isDigit()) {
                builder.append(persianDigits[char.toString().toInt()])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    private fun validateInput(): Boolean {
        val nameInput = activityNameInput.text.toString().trim()
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId

        return nameInput.isNotEmpty() && selectedRadioButtonId != -1
    }


    private fun saveActivity() {
        val nameInput = activityNameInput.text.toString()
        val category = selectedRadioButtonText ?: ""
        val descriptionInput = activityDesInput.text.toString()
        val timeInput = activityTimeInput.text.toString()
        val hourInput = activityHoureInput.text.toString()
        val pin = isCheckBoxPinChecked

        sharedViewModel.setSelectedDate(selectedDate)
        val activity = ActivityEntity(
            aName = nameInput,
            aDescription = descriptionInput,
            aCategory = category,
            aTime = timeInput,
            aHour = hourInput,
            aPin = pin,
            activityId = 0,
            userOwnerId = userOwnerId,
            aDate = selectedDate.timeInMillis,
            completed = false
        )

        activityViewModel.insert(activity)
        Toast.makeText(requireContext(), "فعالیت با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show()

        // تنظیم نوتیفیکیشن در صورتی که hourInput خالی نباشد
        if (hourInput.isNotEmpty()) {
            setNotification(nameInput, selectedDate, hourInput)
        }

        sharedViewModel.setSelectedDate(selectedDate)

        // بازگشت به فرگمنت قبلی
        parentFragmentManager.popBackStack()
    }

    private fun setNotification(activityName: String, date: PersianCalendar, time: String) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date.timeInMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val intent = Intent(requireContext(), ReminderBroadcastReceiver::class.java).apply {
            putExtra("activity_name", activityName)
            putExtra("notification_id", activityName.hashCode())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }
}
