package com.example.taskbin.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.TargetAdapter
import com.example.taskbin.ViewModel.TargetViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class TargetListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TargetAdapter
    private val targetViewModel: TargetViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as MyApplication).userRepository,
            null,
            (requireActivity().application as MyApplication).targetRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_target_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTarget)
        adapter = TargetAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userOwnerId = sharedPreferences.getInt("userOwnerId", 0)

        Log.d("TargetListFragment", "userOwnerId: $userOwnerId")

        targetViewModel.getTargetsByUserOwnerId(userOwnerId).observe(viewLifecycleOwner) { targets ->
            Log.d("TargetListFragment", "Targets: $targets")
            targets?.let { adapter.setTargets(it) }
        }
    }
}
