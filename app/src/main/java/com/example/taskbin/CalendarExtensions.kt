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

fun PersianCalendar.setPersianDate(year: Int, month: Int, day: Int) {
    this[Calendar.YEAR] = year
    this[Calendar.MONTH] = month
    this[Calendar.DAY_OF_MONTH] = day
}


fun PersianCalendar.toGregorian(): Calendar {
    val gregorianCalendar = Calendar.getInstance()
    gregorianCalendar.set(Calendar.YEAR, this.year)
    gregorianCalendar.set(Calendar.MONTH, this.month)
    gregorianCalendar.set(Calendar.DAY_OF_MONTH, this.dayOfMonth)
    return gregorianCalendar
}

fun Calendar.toPersianCalendar(): PersianCalendar {
    val persianCalendar = PersianCalendar()
    persianCalendar.set(Calendar.YEAR, this.get(Calendar.YEAR))
    persianCalendar.set(Calendar.MONTH, this.get(Calendar.MONTH))
    persianCalendar.set(Calendar.DAY_OF_MONTH, this.get(Calendar.DAY_OF_MONTH))
    return persianCalendar
}