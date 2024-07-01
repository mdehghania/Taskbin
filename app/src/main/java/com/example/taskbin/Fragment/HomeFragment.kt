package com.example.taskbin.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aminography.primecalendar.persian.PersianCalendar
import com.example.taskbin.DateAdapter
import com.example.taskbin.R
import com.example.taskbin.View.ActivityListFragment
import com.example.taskbin.View.Profile
import com.example.taskbin.addDay
import com.example.taskbin.setDayOfMonth

class HomeFragment : Fragment() {

    private lateinit var adapter: DateAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var profileBtn: ImageView
    private val dates = mutableListOf<PersianCalendar>()
    private lateinit var selectedDate: PersianCalendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val toDayDateTextView = view.findViewById<TextView>(R.id.toDayDate)
        val today = PersianCalendar()
        val persianDate = "${today.dayOfMonth} ${getPersianMonthName(today.month)} ${today.year}"
        toDayDateTextView.text = persianDate

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        setupDates()

        adapter = DateAdapter(requireContext(), dates) { date ->
            selectedDate = date

        }
        recyclerView.adapter = adapter

        selectToday()

        scrollToSelectedDate()

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                selectToday()
                scrollToSelectedDate()
                handler.postDelayed(this, 24 * 60 * 60 * 1000)
            }
        })

        // اینجا اضافه کردن Fragment به FrameLayout
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ActivityListFragment())
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBtn = view.findViewById(R.id.profileBtn)

        profileBtn.setOnClickListener {
            val intent = Intent(requireContext(), Profile::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupDates() {
        val currentCalendar = PersianCalendar()

        dates.clear()

        currentCalendar.setDayOfMonth(1)
        val startDayOfWeek = currentCalendar.dayOfWeek

        currentCalendar.addDay(-startDayOfWeek)

        val daysInMonth = currentCalendar.monthLength
        for (i in 0 until daysInMonth) {
            val date = currentCalendar.clone() as PersianCalendar
            dates.add(date)
            currentCalendar.addDay(1)
        }

        while (currentCalendar.dayOfWeek != 1) {
            val date = currentCalendar.clone() as PersianCalendar
            dates.add(date)
            currentCalendar.addDay(1)
        }

        dates.reverse()
    }

    private fun selectToday() {
        val today = PersianCalendar()
        selectedDate = today
        val todayIndex = dates.indexOfFirst { it.isSameDay(today) }
        adapter.selectedPosition = todayIndex
        adapter.notifyDataSetChanged()
    }

    private fun scrollToSelectedDate() {
        val today = PersianCalendar()
        val todayIndex = dates.indexOfFirst { it.isSameDay(today) }
        layoutManager.scrollToPosition(todayIndex)
    }

    fun getSelectedDate(): PersianCalendar {
        return selectedDate
    }

    private fun PersianCalendar.isSameDay(other: PersianCalendar): Boolean {
        return this.year == other.year && this.month == other.month && this.dayOfMonth == other.dayOfMonth
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