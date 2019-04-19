package com.aabulhaj.hujiapp.fragments

import Session
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.aabulhaj.hujiapp.Cache
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.ExamAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Course
import com.aabulhaj.hujiapp.data.Exam
import com.aabulhaj.hujiapp.data.getExamURL
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import android.content.Intent
import com.aabulhaj.hujiapp.activities.ExamsDetailsActivity


private const val CACHE_FILENAME = "exams"

class ExamsFragment : RefreshListFragment() {
    private var examsAdapter: ExamAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)

        if (examsAdapter == null) {
            examsAdapter = ExamAdapter(context!!)
            listAdapter = examsAdapter
            loadCache()
            onRefresh()
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.setSelector(android.R.color.transparent)
        (listView.emptyView as TextView).text = getString(R.string.no_exams)
    }

    private fun stopListRefreshing() {
        activity?.runOnUiThread { stopRefreshing() }
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val details = Intent(context, ExamsDetailsActivity::class.java)
        details.putExtra("exam", examsAdapter?.getItem(position))
        activity?.startActivity(details)
    }

    override fun onRefresh() {
        setRefreshing(true)
        if (activity == null) return
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(getExamURL()),
                activity!!, object : StringCallback {

            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                var courseYear = "2019"
                val doc = Jsoup.parse(responseBody)

                try {
                    val yearElements = doc.getElementsByAttributeValue("target", "new")
                    for (element in yearElements) {
                        val elements = element.getElementsByClass("link")
                        if (elements != null && elements.isNotEmpty()) {
                            val yearLink = elements.first().getElementsByClass("link").first().attr("href")
                            val yearWithExtras = yearLink.substring(yearLink.indexOf("yearno="), yearLink.indexOf("yearno=") + 11)
                            courseYear = yearWithExtras.substring(7)
                            break
                        }
                    }
                } catch (e: Exception) {
                    courseYear = "2019"
                }

                val exams = ArrayList<Exam>()
                val tables = doc.getElementsByAttributeValue("cellpadding", "2")
                for (table in tables) {
                    if (table.attr("cellspacing") == "1") {
                        var indexOfSpecialRoom = 0
                        var indexOfRoom = 0
                        var indexOfHour = 0
                        var indexOfDate = 0
                        var indexOfDay = 0
                        var indexOfType = 0
                        var indexOfMoed = 0
                        var indexOfCourseName = 0
                        var indexOfCourseNumber = 0

                        for ((iRow, row) in table.getElementsByTag("tr").withIndex()) {
                            val exam = Exam()
                            exam.course = Course()

                            for ((iColumn, column) in row.getElementsByTag("td").withIndex()) {
                                val text = column.text()
                                if (iRow == 0) {
                                    if (text == "אולם מיוחד*") {
                                        indexOfSpecialRoom = iColumn
                                    } else if (text == "אולם") {
                                        indexOfRoom = iColumn
                                    } else if (text == "שעה") {
                                        indexOfHour = iColumn
                                    } else if (text == "תאריך בחינה") {
                                        indexOfDate = iColumn
                                    } else if (text == "יום") {
                                        indexOfDay = iColumn
                                    } else if (text == "סוג בחינה") {
                                        indexOfType = iColumn
                                    } else if (text == "מועד") {
                                        indexOfMoed = iColumn
                                    } else if (text == "שם קורס") {
                                        indexOfCourseName = iColumn
                                    } else if (text == "קורס") {
                                        indexOfCourseNumber = iColumn
                                    }
                                } else {
                                    if (iColumn == indexOfCourseNumber) {
                                        exam.course!!.number = column.children().first().text()
                                    } else if (iColumn == indexOfCourseName) {
                                        exam.course!!.name = text
                                    } else if (iColumn == indexOfDate) {
                                        exam.dateString = text
                                    } else if (iColumn == indexOfDay) {
                                        continue
                                    } else if (iColumn == indexOfRoom) {
                                        exam.room = text
                                    } else if (iColumn == indexOfSpecialRoom) {
                                        exam.roomsSpecial = text
                                    } else if (iColumn == indexOfMoed) {
                                        exam.setMoed(text!!)
                                    } else if (iColumn == indexOfType) {
                                        if (text != null)
                                            exam.examType = CourseTypeEnum.getCourseTypeEnum(text)
                                    } else if (iColumn == indexOfHour) {
                                        exam.timeString = text
                                    }
                                }
                            }
                            if (iRow > 0) {
                                exam.course!!.year = courseYear.toInt()
                                exam.createDate()
                                exams.add(exam)
                            }
                        }
                    }
                    activity?.runOnUiThread {
                        examsAdapter?.clear()
                        if (exams.isNotEmpty()) {
                            examsAdapter?.addAll(exams)
                        }
                        examsAdapter?.notifyDataSetChanged()
                    }
                    Cache.cacheObject(activity, exams,
                            object : TypeToken<ArrayList<Exam>>() {}.type,
                            Session.getCacheKey(CACHE_FILENAME))
                }
                stopListRefreshing()
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {
                stopListRefreshing()
            }
        })
    }

    private fun loadCache() {
        val data = Cache.loadCachedObject(activity, object : TypeToken<ArrayList<Exam>>() {}.type,
                Session.getCacheKey(CACHE_FILENAME)) ?: return

        val exams = data as ArrayList<Exam>

        activity?.runOnUiThread {
            examsAdapter?.clear()
            examsAdapter?.addAll(exams)
            examsAdapter?.notifyDataSetChanged()
        }
    }
}
