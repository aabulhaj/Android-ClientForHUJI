package com.aabulhaj.hujiapp.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.GPACalculatorAdapter
import com.aabulhaj.hujiapp.data.Grade
import kotlinx.android.synthetic.main.activity_gpacalculator.*


class GPACalculatorActivity : ToolbarActivity() {
    private lateinit var grades: ArrayList<Grade>
    private lateinit var courseAdapter: GPACalculatorAdapter
    private lateinit var footerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpacalculator)

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = getString(R.string.gpaCalc)

        grades = intent.extras!!.get("courses") as ArrayList<Grade>
        courseAdapter = GPACalculatorAdapter(this)
        courseAdapter.addAll(grades)
        courseAdapter.notifyDataSetChanged()


        val footerView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        footerTextView = footerView.findViewById(android.R.id.text1) as TextView
        footerTextView.gravity = Gravity.CENTER
        footerTextView.setTextColor(Color.LTGRAY)

        gpaListView.addFooterView(footerView)
        gpaListView.adapter = courseAdapter

        updateFooterValue()

        gpaListView.setOnItemClickListener { _, _, i, _ ->
            changeGradeDialog(i)
        }
    }

    private fun changeGradeDialog(position: Int) {
        val theme: Int
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            theme = android.R.style.Theme_DeviceDefault_Dialog
        } else {
            theme = android.R.style.Theme_DeviceDefault_Light_Dialog
        }

        val builder = android.app.AlertDialog.Builder(this, theme)
        builder.setMessage(R.string.enter_grade)

        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                resources.displayMetrics).toInt()

        val layout = FrameLayout(this)
        layout.setPadding(padding, padding, padding, padding)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER

        layout.addView(input)

        builder.setView(layout)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val text = input.text.toString()
            val course = courseAdapter.getItem(position)

            if (text.trim() == "") {
                grades[position].grade = -2
                course.grade = -2
            } else {
                val gradeText = text.toInt()
                grades[position].grade = gradeText
                course.grade = gradeText
            }
            course.gradeType = CourseTypeEnum.EXPECTED

            courseAdapter.notifyDataSetChanged()
            updateFooterValue()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
        input.requestFocus()
        builder.show()
    }

    private fun updateFooterValue() {
        var sum = 0f
        var creditPointsSum = 0f

        for (grade in grades) {
            if (grade.grade < 0) continue
            if (grade.course!!.creditPoints!!.toFloat() > 0
                    && (grade.gradeType === CourseTypeEnum.FINAL
                            || grade.gradeType === CourseTypeEnum.CALCULATED
                            || grade.gradeType === CourseTypeEnum.EXPECTED)) {
                creditPointsSum += grade.course!!.creditPoints!!.toFloat()
                sum += grade.course!!.creditPoints!!.toFloat() * grade.grade
            }

        }

        if (grades.isEmpty()) return

        val average = sum / creditPointsSum
        footerTextView.text = getString(R.string.grade_average,
                if (average.isNaN()) 0.00f else average)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}
