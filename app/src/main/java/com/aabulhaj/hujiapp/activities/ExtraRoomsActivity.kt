package com.aabulhaj.hujiapp.activities

import Session
import android.os.Bundle
import android.view.MenuItem
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.ExtraRoomsAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Exam
import com.aabulhaj.hujiapp.data.ExamRooms
import com.aabulhaj.hujiapp.data.getShnatonExamLinkForCourse
import kotlinx.android.synthetic.main.activity_extra_rooms.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call


class ExtraRoomsActivity : ToolbarActivity() {
    private lateinit var adapter: ExtraRoomsAdapter
    private var lastHeaderText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_rooms)

        adapter = ExtraRoomsAdapter(this)
        ExtraRoomsListView.adapter = adapter

        val exam = intent.getSerializableExtra("exam") as Exam
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = exam.course?.name

        val url = getShnatonExamLinkForCourse(exam.course?.number!!, exam.course?.year!!)
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(url),
                this, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)
                val table = doc.allElements.first()

                val dateIndex = 0
                val hourIndex = 1
                val courseCommentsIndex = 2
                val roomIndex = 3
                val moedIndex = 4
                val semesterIndex = 5

                for ((iRow, row) in table.getElementsByTag("tr").withIndex()) {
                    if (iRow <= 3) {
                        continue
                    }

                    val examRooms = ExamRooms()
                    examRooms.courseName = exam.course?.name
                    for ((iCol, column) in row.getElementsByTag("td").withIndex()) {
                        when (iCol) {
                            dateIndex -> examRooms.date = column.text().trim()
                            hourIndex -> column.text()
                            courseCommentsIndex -> column.text()
                            roomIndex -> column.text()
                            moedIndex -> column.text()
                            semesterIndex -> column.text()
                        }
                    }
                    if (iRow > 3) {
                        if (examRooms.semester == null) continue

                        val headerText = "$examRooms.semester - $examRooms.moed"
                        if (!(examRooms.courseComments == null || examRooms.courseComments == "")) {
                            if (lastHeaderText == "") {
                                lastHeaderText = headerText
                                adapter.addSectionHeaderItem(examRooms)
                            } else if (lastHeaderText != headerText) {
                                lastHeaderText = headerText
                                adapter.addSectionHeaderItem(examRooms)
                            }
                        }
                        if (examRooms.courseComments == null
                                || examRooms.courseComments!!.trim().matches("\\s*".toRegex())) {
                            examRooms.courseComments = "N/A"
                        }
                        adapter.addItem(examRooms)
                    }
                }
                runOnUiThread { adapter.notifyDataSetChanged() }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}
