package com.aabulhaj.hujiapp

import com.aabulhaj.hujiapp.data.Course


object CourseToColorMapper {
    private val colorsMap = HashMap<String, Int>()

    fun getColor(course: Course): Int {
        if (!colorsMap.containsKey(course.number)) {
            colorsMap[course.getCourseNumber()] = randomFlatColor()
        }

        return colorsMap[course.getCourseNumber()]!!
    }

    fun setColor(course: Course, color: Int) {
        colorsMap[course.getCourseNumber()] = color
    }
}