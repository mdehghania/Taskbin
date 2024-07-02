package com.example.taskbin.View

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.ActivityViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

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
        adapter = ActivityAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        activityViewModel.getActivitiesByUserOwnerId(userOwnerId).observe(viewLifecycleOwner) { activities ->
            activities?.let { adapter.setActivities(it) }
        }
    }
//    private fun showEditDialog(activity: ActivityEntity) {
//        val dialogView = layoutInflater.inflate(R.layout.layout_dialog_edit_activity, null)
//        val etActivityName = dialogView.findViewById<EditText>(R.id.activityNameInput)
//        val etActivityDescription = dialogView.findViewById<EditText>(R.id.activityDesInput)
//
//        etActivityName.setText(activity.aName)
//        etActivityDescription.setText(activity.aDescription)
//
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("ویرایش فعالیت")
//            .setView(dialogView)
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setPositiveButton("Update") { dialog, _ ->
//                target.tName = etTargetName.text.toString()
//                target.tDesc = etTargetDescription.text.toString()
//
//                activityViewModel.update(target)
//                sortAndNotifyAdapter() // مرتب‌سازی پس از به‌روزرسانی داده‌ها
//            }
//            .show()
//    }
}
