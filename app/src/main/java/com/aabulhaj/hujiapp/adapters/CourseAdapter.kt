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
import com.aabulhaj.hujiapp.views.GradeProgressBar
import kotlinx.android.synthetic.main.course_layout.view.*
import java.util.*


class CourseAdapter(context: Context) : AdvancedArrayAdapter<Grade>(context) {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var progressBar: GradeProgressBar? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.course_layout, parent, false)!!
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
            view.gradeTextView?.visibility = View.GONE
            view.gradeProgressBar?.visibility = View.GONE
        } else {
            setGrade(gradePercentage, displayGrade, view.gradeTextView, view)
        }

        if (grade.gradeType == CourseTypeEnum.UNKNOWN) {
            view.typeLabel.visibility = View.INVISIBLE
            view.courseTypeLabel.visibility = View.INVISIBLE
        } else if (grade.gradeType != null) {
            view.typeLabel.visibility = View.VISIBLE
            view.courseTypeLabel.visibility = View.VISIBLE
            view.courseTypeLabel.text = CourseTypeEnum.getString(grade.gradeType, getContext())
        }

        setHintView(grade, view)
        if (grade.course == null) return view

        view.courseNumberTextView.text = grade.course?.number
        view.courseNameTextView.text = grade.course?.name

        if (!getContext().resources.getBoolean(R.bool.is_rtl)) {
            view.courseNameTextView.gravity = Gravity.LEFT
        }

        if (grade.course?.creditPoints == null || grade.course?.creditPoints == "-1") {
            view.points.text = String.format(Locale.getDefault(), "%d.%d%d", 0, 0, 0)
        } else {
            view.points.text = grade.course?.creditPoints
        }

        return view
    }

    private fun setGrade(percentage: Int, text: String, grade: TextView, view: View) {
        grade.text = text
        grade.visibility = View.VISIBLE
        view.gradeProgressBar?.visibility = View.VISIBLE
        view.gradeProgressBar?.setPercent(percentage, false)
    }

    private fun setHintView(grade: Grade, v: View) {
        v.markCellHint.setShowExtraMarksHint(grade.extraGradesURL != null)
        v.markCellHint.setShowStatisticsHint(grade.statisticsURL != null)
    }

}