package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aminography.primecalendar.persian.PersianCalendar

class SharedViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData<PersianCalendar>()
    val selectedDate: LiveData<PersianCalendar> get() = _selectedDate

    fun setSelectedDate(date: PersianCalendar) {
        _selectedDate.value = date
    }
}