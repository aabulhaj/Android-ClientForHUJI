package com.aabulhaj.hujiapp.fragments

import Session
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.*
import com.aabulhaj.hujiapp.Cache
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.TimetableAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Course
import com.aabulhaj.hujiapp.data.TimeTableClass
import com.aabulhaj.hujiapp.data.TimeTableDay
import com.aabulhaj.hujiapp.data.timeTableUrl
import com.aabulhaj.hujiapp.util.PreferencesUtil
import com.alamkanak.weekview.DateTimeInterpreter
import com.alamkanak.weekview.WeekView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_table.*
import kotlinx.android.synthetic.main.fragment_table.view.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class TableFragment : Fragment(), RefreshableFragment {
    companion object {
        const val SEM_PREF = "table_semester"
        const val CACHE_FILENAME = "timetable"
    }

    private var adapter: TimetableAdapter? = null
    private var currentSemester = 1
    private var semesterMenuButton: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentSemester = PreferencesUtil.getInt(Session.getCacheKey(SEM_PREF), 1)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.time_table_menu, menu)
        semesterMenuButton = menu?.findItem(R.id.semesterChangeButton)

        if (currentSemester == 1) {
            semesterMenuButton?.title = getString(R.string.first_semester)
        } else {
            semesterMenuButton?.title = getString(R.string.second_semester)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.semesterChangeButton) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(null)
            builder.setItems(arrayOf(getString(R.string.first_semester),
                    getString(R.string.second_semester)),
                    DialogInterface.OnClickListener { _, which ->
                        if (which == currentSemester - 1) {
                            return@OnClickListener
                        } else if (which == 0) {
                            item.setTitle(R.string.first_semester)
                            currentSemester = 1
                        } else if (which == 1) {
                            item.setTitle(R.string.second_semester)
                            currentSemester = 2
                        }
                        loadCache(weekView)
                        item.isEnabled = false
                        onSemesterChange()
                        PreferencesUtil.putInt(Session.getCacheKey(SEM_PREF), currentSemester)
                    })
            builder.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

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
            loadCache(weekView)
            onSemesterChange()
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

    private fun onSemesterChange() {
        val timeTableDays = ArrayList<TimeTableDay>()
        timeTableDays.add(TimeTableDay(0))
        timeTableDays.add(TimeTableDay(1))
        timeTableDays.add(TimeTableDay(2))
        timeTableDays.add(TimeTableDay(3))
        timeTableDays.add(TimeTableDay(4))

        Session.callRequest(fun() = Session.hujiApiClient.getTimeTable(
                Session.getSessionUrl(timeTableUrl(currentSemester))),
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
                        for (loc in 1 until courseTypeAndLoc.size) {
                            timeTableClass.classLocation += courseTypeAndLoc[loc] + " "
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
                    semesterMenuButton?.setEnabled(true)
                }

                Cache.cacheObject(activity, timeTableDays,
                        object : TypeToken<ArrayList<TimeTableDay>>() {}.type,
                        CACHE_FILENAME + currentSemester.toString())
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

    private fun loadCache(weekView: WeekView) {
        val data = Cache.loadCachedObject(activity,
                object : TypeToken<ArrayList<TimeTableDay>>() {}.type,
                CACHE_FILENAME + currentSemester.toString()) ?: return


        val days = data as ArrayList<TimeTableDay>

        activity?.runOnUiThread {
            adapter?.setTimetableDays(days)
            weekView.notifyDatasetChanged()
        }
    }

    override fun refresh() {

    }

    override fun getFragment(): Fragment {
        return this
    }
}
