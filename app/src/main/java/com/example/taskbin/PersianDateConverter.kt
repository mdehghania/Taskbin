import com.aminography.primecalendar.persian.PersianCalendar

object PersianDateConverter {

    fun convertToPersianDate(timestamp: Long): String {
        val calendar = PersianCalendar()
        calendar.timeInMillis = timestamp
        return String.format("%04d/%02d/%02d", calendar.year, calendar.month + 1, calendar.dayOfMonth)
    }
}
