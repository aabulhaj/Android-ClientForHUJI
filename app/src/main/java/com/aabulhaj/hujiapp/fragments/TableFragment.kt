package com.aabulhaj.hujiapp.fragments

import Session
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.TimetableAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Course
import com.aabulhaj.hujiapp.data.TimeTableClass
import com.aabulhaj.hujiapp.data.TimeTableDay
import com.alamkanak.weekview.DateTimeInterpreter
import com.alamkanak.weekview.WeekView
import kotlinx.android.synthetic.main.fragment_table.*
import kotlinx.android.synthetic.main.fragment_table.view.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class TableFragment : Fragment() {
    private var adapter: TimetableAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_table, container, false)

        val weekView = view.weekView
        setWeekViewDates(weekView)

        if (adapter == null) {
            val c = Calendar.getInstance()
            val today = c.get(Calendar.DAY_OF_WEEK)
            if (today == 6 || today == 7) {
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                weekView.goToDate(c)
            }
            adapter = TimetableAdapter()
        }

        if (weekView.monthChangeListener == null) {
            weekView.monthChangeListener = adapter
        }

        weekView.hourHeight = 7 * weekView.hourHeight / 10
        weekView.columnGap = 3

        weekView.dayBackgroundColor = Color.WHITE
        weekView.todayBackgroundColor = Color.WHITE

        weekView.dateTimeInterpreter = object : DateTimeInterpreter {
            override fun interpretDate(date: Calendar): String {
                val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
                return sdf.format(date.time).toUpperCase()
            }

            override fun interpretTime(hour: Int, minutes: Int): String {
                val is24 = DateFormat.is24HourFormat(context)
                val format = if (is24) "HH:mm" else "h a"

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, 0)

                val date = calendar.time

                val sdf = SimpleDateFormat(format, Locale.getDefault())
                return sdf.format(date)
            }
        }

        val displayMetrics = context!!.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        weekView.numberOfVisibleDays = Math.round(dpWidth / 180)

        getTimeTable()

        return view
    }

    private fun setWeekViewDates(weekView: WeekView) {
        val minDate = Calendar.getInstance(Locale.US)
        val maxDate = Calendar.getInstance(Locale.US)

        minDate.time = Date()
        maxDate.time = Date()

        minDate.set(Calendar.DAY_OF_WEEK, 1)
        maxDate.set(Calendar.DAY_OF_WEEK, 5)

        weekView.minDate = minDate
        weekView.maxDate = maxDate

        // Classes are between 8 AM and 22 PM.
        weekView.setMinTime(8)
        weekView.setMaxTime(22)
    }

    private fun getTimeTable() {
        val timeTableDays = ArrayList<TimeTableDay>()
        timeTableDays.add(TimeTableDay(0))
        timeTableDays.add(TimeTableDay(1))
        timeTableDays.add(TimeTableDay(2))
        timeTableDays.add(TimeTableDay(3))
        timeTableDays.add(TimeTableDay(4))

        Session.callRequest(fun() = Session.hujiApiClient.getFirstSemesterTimeTable(),
                activity!!, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)
                val tableRows = doc.select("table")[10].select("tbody")[0].select("tr").drop(2)

                for (row in tableRows) {
                    val classesInRow = ArrayList<TimeTableClass>()
                    val rowCols = row.select("td")

                    var hour: String? = null
                    for (i in 0 until rowCols.size) {
                        val col = rowCols[i]
                        if (i == rowCols.size - 1) {
                            hour = col.text()
                            continue
                        }
                        val text = col.ownText().trim()

                        // Same class as above.
                        if (text == "כנ\"ל") {
                            classesInRow.add(TimeTableClass.aboveClassObject())
                            continue
                        }

                        val linkElem = col.getElementsByTag("a").last()

                        // No class in this hour.
                        if (linkElem == null || text.isEmpty()) {
                            classesInRow.add(TimeTableClass.placeHolderClass())
                            continue
                        }

                        val linkToCourse = linkElem.attr("href")
                        val linkComponents = getURLBetweenJS(linkToCourse)!!.split("/")

                        val courseNum = linkComponents.last()
                        val courseName = linkElem.text().trim()

                        if (courseName.isEmpty()) {
                            classesInRow.add(TimeTableClass.placeHolderClass())
                            continue
                        }

                        val course = Course(courseName, courseNum)

                        val courseTypeAndLoc = text.split(" ").map { it.trim() }

                        val timeTableClass = TimeTableClass(course)

                        if (courseTypeAndLoc.isEmpty()) {
                            timeTableClass.classLocation = ""
                            classesInRow.add(timeTableClass)
                            continue
                        }

                        timeTableClass.type = TimeTableClass.Type.CLASS
                        if (courseTypeAndLoc[0].startsWith("תרג")) {
                            timeTableClass.type = TimeTableClass.Type.TIRUGL
                        } else if (courseTypeAndLoc[0].startsWith("מעב")) {
                            timeTableClass.type = TimeTableClass.Type.LAB
                        }

                        timeTableClass.classLocation = ""
                        if (courseTypeAndLoc.size >= 2) {
                            timeTableClass.classLocation = courseTypeAndLoc[1]
                        }

                        classesInRow.add(timeTableClass)
                    }

                    classesInRow.reverse()
                    for ((i, timeTableClass) in classesInRow.withIndex()) {
                        timeTableClass.setHourFromString(hour!!, i)
                        timeTableDays[i].addTimetableClass(timeTableClass)
                    }
                }

                adapter?.clear()
                if (timeTableDays.isNotEmpty()) {
                    adapter?.setTimetableDays(timeTableDays)
                }

                activity?.runOnUiThread {
                    weekView.notifyDatasetChanged()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {}
        })
    }

    fun getURLBetweenJS(s: String?): String? {
        if (s == null) {
            return null
        }
        val p = Pattern.compile(".*\'(.*)\'\\);")
        val m = p.matcher(s)
        return if (m.find()) {
            m.group(1)
        } else null
    }
}
