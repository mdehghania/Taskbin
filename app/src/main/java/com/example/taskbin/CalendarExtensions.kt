package com.example.taskbin

import com.aminography.primecalendar.persian.PersianCalendar
import java.util.Calendar

fun PersianCalendar.setDayOfMonth(day: Int) {
    this[Calendar.DAY_OF_MONTH] = day
}

fun PersianCalendar.addDay(days: Int) {
    this.add(Calendar.DAY_OF_MONTH, days)
}

fun PersianCalendar.addMonth(months: Int) {
    this.add(Calendar.MONTH, months)
}
