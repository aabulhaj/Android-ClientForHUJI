package com.aabulhaj.hujiapp.adapters

import com.aabulhaj.hujiapp.data.TimeTableDay
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent


class TimetableAdapter : MonthLoader.MonthChangeListener {
    private var events: ArrayList<WeekViewEvent>? = null
    private var timeTableDays = ArrayList<TimeTableDay>()

    fun setTimetableDay(index: Int, day: TimeTableDay) {
        timeTableDays.add(index, day)
    }

    fun setTimetableDays(days: List<TimeTableDay>) {
        timeTableDays.clear()
        events = null
        timeTableDays.addAll(days)
    }

    fun hasData(): Boolean {
        return timeTableDays.isNotEmpty()
    }

    override fun onMonthChange(newYear: Int, newMonth: Int): List<WeekViewEvent> {
        if (events == null) {
            events = ArrayList()

            for (day in timeTableDays) {
                events!!.addAll(day.getClasses())
            }
        }

        return events!!
    }

    fun clear() {
        events = null
        timeTableDays = ArrayList()
    }
}