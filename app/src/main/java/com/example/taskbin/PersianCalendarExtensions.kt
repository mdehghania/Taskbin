package com.example.taskbin

import com.aminography.primecalendar.persian.PersianCalendar

fun PersianCalendar.isSameDay(other: PersianCalendar): Boolean {
    return this.year == other.year &&
            this.month == other.month &&
            this.dayOfMonth == other.dayOfMonth
}
