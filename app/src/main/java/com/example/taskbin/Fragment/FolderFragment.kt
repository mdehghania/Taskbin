package com.example.taskbin.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.R
import com.example.taskbin.View.Profile
import com.example.taskbin.View.ProjectListFragment


class FolderFragment : Fragment() {
    private lateinit var profileBtn: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_folder, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBtn = view.findViewById(R.id.profileBtn)

        profileBtn.setOnClickListener {
            val intent = Intent(requireContext(), Profile::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }


        val toDayDateTextView = view.findViewById<TextView>(R.id.toDayDate)
        val today = PersianCalendar()
        val persianDate = "${today.dayOfMonth} ${getPersianMonthName(today.month)} ${today.year}"
        toDayDateTextView.text = persianDate

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewP, ProjectListFragment())
                .commit()
        }

    }
    private fun getPersianMonthName(month: Int): String {
        return when (month) {
            0 -> "فروردین"
            1 -> "اردیبهشت"
            2 -> "خرداد"
            3 -> "تیر"
            4 -> "مرداد"
            5 -> "شهریور"
            6 -> "مهر"
            7 -> "آبان"
            8 -> "آذر"
            9 -> "دی"
            10 -> "بهمن"
            11 -> "اسفند"
            else -> ""
        }
    }
}