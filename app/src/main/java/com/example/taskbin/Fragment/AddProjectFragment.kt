package com.example.taskbin.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.taskbin.R

class AddProjectFragment : Fragment() {

    private lateinit var stagesContainer: LinearLayout
    private var stageCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_project, container, false)
        stagesContainer = view.findViewById(R.id.stagesContainer)
        val addStageButton = view.findViewById<ImageButton>(R.id.addStages4)

        addStageButton.setOnClickListener {
            addNewStage()
        }

        return view
    }

    private fun addNewStage() {
        stageCount++

        val newStageLayout = RelativeLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10
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
                marginEnd = 15.dpToPx()
                topMargin = 8.dpToPx()
                addRule(RelativeLayout.START_OF, newEditText.id)
            }
            background = resources.getDrawable(R.drawable.stages, null)
            setOnClickListener {
                // Here you can add the action for the ImageButton if needed
            }
        }

        newStageLayout.addView(newEditText)
        newStageLayout.addView(newImageButton)

        stagesContainer.addView(newStageLayout)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
