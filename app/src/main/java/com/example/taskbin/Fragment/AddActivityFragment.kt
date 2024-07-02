package com.example.taskbin.View

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ActivityViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import java.util.Calendar

class AddActivityFragment : Fragment() {
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

        activityTimeInput.setOnClickListener {
            showTimePickerDialog(activityTimeInput)
        }

        activityHoureInput.setOnClickListener {
            showTimePickerDialog(activityHoureInput)
        }

        btnBackAddActivity.setOnClickListener {
            onBackPressed()
        }

        btnSaveActivity.setOnClickListener {
            if (validateInput()) {
                saveActivity()
                activity?.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Read userOwnerId from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        // Get selected date from arguments
        selectedDate = arguments?.getSerializable("selectedDate") as? PersianCalendar ?: PersianCalendar()

        btnBackAddActivity.setOnClickListener {
            onBackPressed()
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
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            targetEditText.setText(convertToPersian(formattedTime))
        }

        TimePickerDialog(requireContext(), timeSetListener, hour, minute, true).show()
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
        Toast.makeText(requireContext(), "Activity saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun onBackPressed() {
        if (!validateInput()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        } else {
            activity?.onBackPressed()
        }
    }
}
