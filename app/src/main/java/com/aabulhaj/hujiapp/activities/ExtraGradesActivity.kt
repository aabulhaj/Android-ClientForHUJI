package com.aabulhaj.hujiapp.activities

import Session
import android.os.Bundle
import android.view.MenuItem
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.ExtraGradesAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Course
import com.aabulhaj.hujiapp.data.ExtraGrades
import com.aabulhaj.hujiapp.data.Grade
import kotlinx.android.synthetic.main.activity_extra_grades.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call


class ExtraGradesActivity : ToolbarActivity() {
    private var extraGradesAdapter: ExtraGradesAdapter? = null
    private var lastSemester: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_grades)

        extraGradesAdapter = ExtraGradesAdapter(this)
        extraGradesListView.adapter = extraGradesAdapter

        val grade = intent.getSerializableExtra("grade") as Grade

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = grade.course?.name
        showExtraGrades(grade.extraGradesURL!!, grade)
    }

    private fun showExtraGrades(url: String, grade: Grade) {
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(url),
                this, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)
                val tables = doc.getElementsByAttributeValue("cellpadding", "2")

                for (table in tables) {
                    if (table.attr("cellspacing") == "1") {
                        var indexOfGradeType = 0
                        var indexOfMoed = 0
                        var indexOfSemester = 0
                        var indexOfGrade = 0

                        for ((iRow, row) in table.getElementsByTag("tr").withIndex()) {
                            val extraGrades = ExtraGrades()
                            extraGrades.course = Course()

                            for ((iColumn, column) in row.getElementsByTag("td").withIndex()) {
                                val text = column.text()

                                if (iRow == 0) {
                                    if (text == "ציון") {
                                        indexOfGrade = iColumn
                                    } else if (text == "תקופה") {
                                        indexOfSemester = iColumn
                                    } else if (text == "מועד") {
                                        indexOfMoed = iColumn
                                    } else if (text == "סוג ציון") {
                                        indexOfGradeType = iColumn
                                    }
                                    continue
                                }
                                if (iColumn == indexOfGrade) {
                                    if (text == "עבר") {
                                        extraGrades.grade = Grade.PASS
                                    } else if (text == "נכשל") {
                                        extraGrades.grade = Grade.FAIL
                                    } else if (text == "פטור") {
                                        extraGrades.grade = Grade.EXEMPT
                                    } else {
                                        extraGrades.grade = -1
                                        if (text.isNotEmpty()) {
                                            extraGrades.grade = text.toInt()
                                        }
                                    }
                                } else if (iColumn == indexOfGradeType) {
                                    if (text == null || text.trim() == "") continue
                                    extraGrades.gradeType = CourseTypeEnum.getCourseTypeEnum(text)
                                } else if (iColumn == indexOfMoed) {
                                    extraGrades.moed = text
                                } else if (iColumn == indexOfSemester) {
                                    extraGrades.semester = text
                                }
                            }
                            extraGrades.course?.name = grade.course?.name
                            extraGrades.course?.number = grade.course?.number
                            if (iRow > 0) {
                                if (lastSemester == "") {
                                    lastSemester = extraGrades.semester!!
                                    extraGradesAdapter?.addSectionHeaderItem(extraGrades)
                                } else if (extraGrades.semester != lastSemester) {
                                    lastSemester = extraGrades.semester!!
                                    extraGradesAdapter?.addSectionHeaderItem(extraGrades)
                                }
                                extraGradesAdapter?.addItem(extraGrades)
                            }
                        }
                    }

                    runOnUiThread {
                        extraGradesAdapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}
