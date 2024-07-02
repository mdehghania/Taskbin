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
import com.example.taskbin.View.TargetListFragment

class TrapgyFragment : Fragment() {
    private lateinit var profileBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trapgy, container, false)
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


        // Adding TargetListFragment to FrameLayout
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.frame_container_activity, TargetListFragment())
                .commit()
        }
    }
    private fun getPersianMonthName(month: Int): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }
}
