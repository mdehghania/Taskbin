package com.example.taskbin.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ProjectViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class AddProjectFragment : Fragment() {

    private lateinit var stagesContainer: LinearLayout
    private var stageCount = 0

    private lateinit var projectNameInput: EditText
    private lateinit var projectHourInput: EditText
    private lateinit var projectTimeInput: EditText
    private lateinit var projectDateInput: EditText
    private lateinit var projectPinCheckBox: CheckBox
    private var userOwnerId: Int = 0

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
        val projectPin = projectPinCheckBox.isChecked

        if (projectName.isEmpty() || projectHour.isEmpty() || projectTime.isEmpty() || projectDate.isEmpty()) {
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
            pPin = projectPin,
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
        activity?.onBackPressed()
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}
