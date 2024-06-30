package com.example.taskbin.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.TargetViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class AddTargetFragment : Fragment() {
    private lateinit var btnBackAddTarget: ImageView
    private lateinit var btnSaveTarget: Button
    private lateinit var targetNameInput: EditText
    private lateinit var targetDesInput: EditText
    private var userOwnerId: Int = 0

    private val targetViewModel: TargetViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            (requireActivity().application as MyApplication).activityRepository,
            (requireActivity().application as MyApplication).targetRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_target, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBackAddTarget = view.findViewById(R.id.btnBackAddTarget)
        btnSaveTarget = view.findViewById(R.id.btnSaveTarget)
        targetDesInput = view.findViewById(R.id.targetDesInput)
        targetNameInput = view.findViewById(R.id.targetNameInput)

        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        btnBackAddTarget.setOnClickListener {
            activity?.onBackPressed() // بازگشت به فرگمنت قبلی
        }
        btnSaveTarget.setOnClickListener {
            saveActivity()
            activity?.onBackPressed()
        }
    }

    private fun saveActivity() {
        val nameInput = targetNameInput.text.toString()
        val descriptionInput = targetDesInput.text.toString()

        if (nameInput.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an activity name", Toast.LENGTH_SHORT).show()
            return
        }

        if (userOwnerId == 0) {
            Toast.makeText(requireContext(), "User ID is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        val target = TargetEntity(
            tName = nameInput, tDesc = descriptionInput, userOwnerId = userOwnerId, completed = false
        )
        targetViewModel.insert(target)
        Toast.makeText(requireContext(), "Successful!", Toast.LENGTH_SHORT).show()
    }
}
