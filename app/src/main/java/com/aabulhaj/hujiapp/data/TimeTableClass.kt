package com.aabulhaj.hujiapp.data

import android.graphics.Color
import com.alamkanak.weekview.WeekViewEvent
import java.util.*
import com.aabulhaj.hujiapp.CourseToColorMapper



class TimeTableClass : WeekViewEvent {
    var course: Course
    var startDate: Date? = null
    var endDate: Date? = null
    var classLocation: String? = null
    var number: String? = null
    var hujiHour: String? = null
    var type: Type? = null
    private var classStartTime: Calendar? = null
    private var classEndTime: Calendar? = null

    enum class Type {
        CLASS, TIRUGL, LAB, UNKNOWN
    }

    constructor(course: Course) {
        this.course = course
    }

    fun isEmptyClass(): Boolean {
        return course.isEmptyClass()
    }

    fun isAboveClass(): Boolean {
        return course.aboveClass
    }

    fun setHourFromString(hour: String, day: Int) {
        hujiHour = hour

        val hourParts = hour.split("-")

        val calendar = Calendar.getInstance(Locale.US)
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, hourParts[0].toInt())
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, day + 1)

        startDate = calendar.time
        calendar.add(Calendar.HOUR, 1)
        endDate = calendar.time
    }

    override fun toString(): String {
        if (isAboveClass()) {
            return "Above class"
        } else if (isEmptyClass()) {
            return "Empty class"
        }
        return course.number + " " + course.name
    }

    override fun getStartTime(): Calendar {
        if (classStartTime == null) {
            classStartTime = dateToCalendar(startDate!!)
        }

        return classStartTime!!
    }

    override fun getEndTime(): Calendar {
        if (classEndTime == null) {
            classEndTime = dateToCalendar(endDate!!)
        }

        return classEndTime!!
    }

    override fun getName(): String {
        return course.name!!
    }

    override fun getColor(): Int {
        val color = CourseToColorMapper.getColor(course)
        return Color.argb(51, Color.red(color), Color.green(color), Color.blue(color))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other.javaClass != javaClass) return false

        other as TimeTableClass

        if (course != other.course) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (classLocation != other.classLocation) return false
        if (type != other.type) return false
        return hujiHour == other.hujiHour
    }

    override fun hashCode(): Int {
        var result = course.hashCode()
        result = 31 * result + startDate!!.hashCode()
        result = 31 * result + endDate!!.hashCode()
        result = 31 * result + classLocation!!.hashCode()
        result = 31 * result + type!!.hashCode()
        result = 31 * result + hujiHour!!.hashCode()
        return result
    }

    override fun getIdentifier(): String {
        return hashCode().toString()
    }

    override fun getLocation(): String {
        return "\n" + classLocation!!
    }

    fun getClassNumber(): String {
        return number!!
    }


    companion object {
        fun placeHolderClass(): TimeTableClass {
            return TimeTableClass(Course(null, null))
        }

        fun aboveClassObject(): TimeTableClass {
            return TimeTableClass(Course(true))
        }


        private fun dateToCalendar(date: Date): Calendar {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal
        }
    }

}