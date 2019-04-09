package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.NoteBook
import kotlinx.android.synthetic.main.notebook_row.view.*


class NoteBooksAdapter(context: Context) : AdvancedArrayAdapter<NoteBook>(context) {
    private val inflater = LayoutInflater.from(context) as LayoutInflater


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v: View? = convertView
        if (v == null) {
            v = inflater.inflate(R.layout.notebook_row, parent, false)
        }

        val noteBook = getItem(position)
        val isRTL = getContext().resources.getBoolean(R.bool.is_rtl)

        if (noteBook.courseName != null && isRTL) {
            v?.notebookCourseName?.gravity = Gravity.START
        } else if (noteBook.courseName != null) {
            v?.notebookCourseName?.gravity = Gravity.LEFT
        }

        v?.notebookCourseName?.text = noteBook.courseName
        v?.notebookCourseNum?.text = noteBook.courseNumber
        v?.notebookDate?.text = noteBook.date
        v?.notebookTypeLabel?.text = CourseTypeEnum.getString(noteBook.courseType, getContext())

        return v
    }
}
