package com.aabulhaj.hujiapp.data

import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.Moed
import java.io.Serializable
import java.util.*


class Exam : Serializable {
    var course: Course? = null
    var examType: CourseTypeEnum? = null
    var room: String? = null
    var roomsSpecial: String? = null
    var dateString: String? = null
    var timeString: String? = null
    var moed: Moed? = null
    var date: Date? = null

    fun setMoed(moed: String) {
        when (moed) {
            "א" -> this.moed = Moed.A
            "ב" -> this.moed = Moed.B
            "ג" -> this.moed = Moed.C
        }
    }

    fun createDate() {
        if (dateString != null) {
            val dateComps = dateString!!.split("/")
            val calendar = Calendar.getInstance(Locale.getDefault())
            var hour = 0
            var minute = 0
            if (timeString != null && timeString!!.trim().isNotEmpty()) {
                val timeComps = timeString!!.split(":")
                hour = Integer.parseInt(timeComps[0])
                minute = Integer.parseInt(timeComps[1])
            } else {
                timeString = null
            }

            calendar.set(Integer.parseInt(dateComps[2]), Integer.parseInt(dateComps[1]) - 1,
                    Integer.parseInt(dateComps[0]), hour, minute)
            date = Date(calendar.timeInMillis)

            timeString = null
        }
    }
}