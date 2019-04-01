package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.Grade
import kotlinx.android.synthetic.main.gpa_calc_row.view.*
import java.util.*


class GPACalculatorAdapter(context: Context) : AdvancedArrayAdapter<Grade>(context) {
    private val inflater = LayoutInflater.from(context) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v = convertView
        if (v == null) {
            v = inflater.inflate(R.layout.gpa_calc_row, parent, false)!!
        }

        val grade = getItem(position)

        var gradePercentage = 0
        var displayGrade = ""

        if (grade.grade == Grade.PASS) {
            gradePercentage = 100
            displayGrade = "עבר"
        } else if (grade.grade == Grade.FAIL) {
            gradePercentage = 0
            displayGrade = "נכשל"
        } else if (grade.grade == Grade.EXEMPT) {
            gradePercentage = 100
            displayGrade = "פטור"
        } else if (grade.grade > -1) {
            gradePercentage = grade.grade
            displayGrade = grade.grade.toString()
        } else if (!grade.statisticsURL.isNullOrEmpty() || !grade.extraGradesURL.isNullOrEmpty()) {
            gradePercentage = 0
            displayGrade = "-"
        }

        if (grade.grade == -1 && displayGrade == "") {
            v.gpaGradeTextView?.visibility = View.GONE
            v.gpaGradeProgressBar?.visibility = View.GONE
        } else {
            setGrade(gradePercentage, displayGrade, v.gpaGradeTextView, v)
        }

        if (grade.gradeType != null && grade.gradeType !== CourseTypeEnum.UNKNOWN) {
            v.gpaCourseTypeLabel.text = CourseTypeEnum.getString(grade.gradeType, getContext())
            v.gpaCourseTypeLabel.visibility = View.VISIBLE
            v.gpaTypeTextView.visibility = View.VISIBLE
        } else if (grade.gradeType != null) {
            v.gpaCourseTypeLabel.visibility = View.GONE
            v.gpaTypeTextView.visibility = View.GONE
        }

        if (grade.course == null) return v

        v.courseNumber.text = grade.course?.number
        v.courseName.text = grade.course?.name

        if (!getContext().resources.getBoolean(R.bool.is_rtl)) {
            v.courseName.gravity = Gravity.LEFT
        }

        if (grade.course?.creditPoints == null || grade.course?.creditPoints == "-1") {
            v.pointsTextView.text = String.format(Locale.getDefault(), "%d.%d%d", 0, 0, 0)
        } else {
            v.pointsTextView.text = grade.course?.creditPoints
        }

        return v
    }

    private fun setGrade(percentage: Int, text: String, grade: TextView, v: View) {
        grade.text = text
        grade.visibility = View.VISIBLE
        v.gpaGradeProgressBar.visibility = View.VISIBLE
        v.gpaGradeProgressBar.setPercent(percentage, false)
    }
}
