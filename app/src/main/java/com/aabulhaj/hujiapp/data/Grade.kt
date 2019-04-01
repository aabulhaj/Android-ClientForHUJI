package com.aabulhaj.hujiapp.data

import com.aabulhaj.hujiapp.CourseTypeEnum
import java.io.Serializable

class Grade : Serializable {
    companion object {
        const val PASS = 201
        const val FAIL = 202
        const val EXEMPT = 203
    }

    var course: Course? = null
    var grade: Int = -1
    var gradeType: CourseTypeEnum? = null
    var extraGradesURL: String? = null
    var statisticsURL: String? = null
}