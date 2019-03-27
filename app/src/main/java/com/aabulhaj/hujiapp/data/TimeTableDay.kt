package com.aabulhaj.hujiapp.data

import android.content.Context
import com.aabulhaj.hujiapp.R
import java.util.*


class TimeTableDay {
    private val timeTableClasses: ArrayList<TimeTableClass> = ArrayList()
    private var day: Int

    constructor(day: Int) {
        this.day = day
    }

    fun addTimetableClass(timetableClass: TimeTableClass) {
        if (timetableClass.isAboveClass()) {
            val date = timeTableClasses.last().endDate
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            timeTableClasses.last().endDate = calendar.time
        } else if (!timetableClass.isEmptyClass()) {
            timeTableClasses.add(timetableClass)
        }
    }

    fun getClassAtIndex(position: Int): TimeTableClass {
        return timeTableClasses[position]
    }

    fun size(): Int {
        return timeTableClasses.size
    }

    fun getDayString(context: Context): String? {
        val days = intArrayOf(R.string.sunday, R.string.monday,
                R.string.tuesday, R.string.wednesday, R.string.thursday)

        return if (day >= 0 && day < days.size)
            context.getString(days[day])
        else
            null
    }

    fun getClasses(): ArrayList<TimeTableClass> {
        return timeTableClasses
    }
}