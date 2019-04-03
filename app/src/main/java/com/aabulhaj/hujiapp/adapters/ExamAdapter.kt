package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.Moed
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.Exam
import kotlinx.android.synthetic.main.course_layout.view.*
import kotlinx.android.synthetic.main.exam_layout.view.*

class ExamAdapter(context: Context) : AdvancedArrayAdapter<Exam>(context) {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.exam_layout, parent, false)!!
        }

        val exam = getItem(position)

        view.examCourseNumber.text = exam.course?.number
        view.examCourseName.text = exam.course?.name

        if (!getContext().resources.getBoolean(R.bool.is_rtl)) {
            view.examCourseName.gravity = Gravity.LEFT
        }

        if (exam.examType != null) {
            if (exam.examType == CourseTypeEnum.UNKNOWN) {
                view.examTypeLabel.visibility = View.INVISIBLE
                view.examType.visibility = View.INVISIBLE

            } else {
                view.examTypeLabel.visibility = View.VISIBLE
                view.examType.visibility = View.VISIBLE
                view.examType.text = CourseTypeEnum.getString(exam.examType, getContext())
            }
        }

        view.examDate.text = exam.dateString

        if (exam.room != null && exam.room?.trim() != "") {
            view.examRoom.text = exam.room
            view.examRoom.setTextColor(Color.BLACK)
        } else {
            view.examRoom.text = "N/A"
            view.examRoom.setTextColor(Color.GRAY)
        }

        if (exam.roomsSpecial != null && exam.roomsSpecial?.trim() != "") {
            view.examSpecialRoom.text = exam.roomsSpecial
            view.examSpecialRoom.setTextColor(Color.BLACK)
        } else {
            view.examSpecialRoom.text = "N/A"
            view.examSpecialRoom.setTextColor(Color.GRAY)
        }

        view.examMoed.text = Moed.getMoedString(exam.moed, getContext())


        return view
    }
}