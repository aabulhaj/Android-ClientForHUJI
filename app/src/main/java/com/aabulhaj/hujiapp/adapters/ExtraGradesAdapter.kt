package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.ExtraGrades
import com.aabulhaj.hujiapp.data.Grade
import de.halfbit.pinnedsection.PinnedSectionListView
import kotlinx.android.synthetic.main.extra_grade_cell_layout.view.*
import java.util.*


class ExtraGradesAdapter(private val context: Context) : BaseAdapter(),
        PinnedSectionListView.PinnedSectionListAdapter {
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1

    private val data = ArrayList<ExtraGrades>()
    private val sectionHeader = TreeSet<Int>()

    private var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun addItem(item: ExtraGrades) {
        data.add(item)
    }

    fun addSectionHeaderItem(item: ExtraGrades) {
        sectionHeader.add(data.size)
        data.add(item)
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): ExtraGrades {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val rowType = getItemViewType(position)

        if (view == null) {
            if (rowType == TYPE_ITEM) {
                view = inflater.inflate(R.layout.extra_grade_cell_layout, parent, false)!!
            } else {
                view = inflater.inflate(R.layout.header_row, parent, false)!!
                view.isClickable = false
                view.findViewById<TextView>(android.R.id.text1).text = getItem(position).semester
            }
        }

        if (rowType == TYPE_ITEM) {
            val extraGrade = getItem(position)

            var gradePercentage = 0
            var displayGrade = ""

            if (extraGrade.grade == Grade.PASS) {
                gradePercentage = 100
                displayGrade = "עבר"
            } else if (extraGrade.grade == Grade.FAIL) {
                gradePercentage = 0
                displayGrade = "נכשל"
            } else if (extraGrade.grade > -1) {
                gradePercentage = extraGrade.grade
                displayGrade = extraGrade.grade.toString()
            }

            if (gradePercentage == 0 && displayGrade == "") {
                view.extraGradeTextView.visibility = View.INVISIBLE
                view.extraGradesProgressBar.visibility = View.INVISIBLE
            } else {
                view.extraGradeTextView.visibility = View.VISIBLE
                setGrade(gradePercentage, displayGrade, view.extraGradeTextView, view)
            }

            view.extraGradeCourseNum.text = extraGrade.course?.number
            view.extraGradeCouseName.text = extraGrade.course?.name

            if (!context.resources.getBoolean(R.bool.is_rtl)) {
                view.extraGradeCouseName.gravity = Gravity.LEFT
            }

            view.extraGradeCourseTypeLabel.text = CourseTypeEnum.getString(extraGrade.gradeType,
                    context)

            if (extraGrade.moed != null) {
                if (extraGrade.moed == "א") {
                    view.extraGradeMoed.text = context.getString(R.string.moed_a)
                } else if (extraGrade.moed == "ב") {
                    view.extraGradeMoed.text = context.getString(R.string.moed_b)
                } else if (extraGrade.moed == "ג") {
                    view.extraGradeMoed.text = context.getString(R.string.moed_c)
                }
            }
        }

        return view
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }

    private fun setGrade(percentage: Int, text: String, grade: TextView, view: View) {
        grade.text = text
        grade.visibility = View.VISIBLE
        view.extraGradesProgressBar?.visibility = View.VISIBLE
        view.extraGradesProgressBar?.setPercent(percentage, false)
    }
}