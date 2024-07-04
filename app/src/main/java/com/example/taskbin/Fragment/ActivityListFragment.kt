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
import com.aminography.primecalendar.persian.PersianCalendar
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
    }

    private fun PersianCalendar.isSameDay(other: PersianCalendar): Boolean {
        return this.year == other.year && this.month == other.month && this.dayOfMonth == other.dayOfMonth
    }
}