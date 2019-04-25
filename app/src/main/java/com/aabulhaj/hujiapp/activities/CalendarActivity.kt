package com.aabulhaj.hujiapp.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.WindowManager
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.AcademicCalendarAdapter
import com.aabulhaj.hujiapp.data.AcademicCalendarObject
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_calendar.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val DATE_KEY = "Cal"

class CalendarActivity : AppCompatActivity() {
    private var adapter: AcademicCalendarAdapter? = null
    private val events = ArrayList<Event>()

    private lateinit var currDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme_NoActionBarTranslucent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        setSupportActionBar(calendarToolbar)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        calendarView.setLocale(TimeZone.getDefault(), Locale.US)
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        currDate = Date()
        val savedDate = savedInstanceState?.getSerializable(DATE_KEY)
        if (savedDate != null) {
            currDate = savedDate as Date
        }

        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(currDate.time)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(currDate.time)

        calendarView.setCurrentDate(currDate)

        supportActionBar?.title = "$month $year"

        adapter = AcademicCalendarAdapter(this)

        parseEvents()

        calendarEventsListView.adapter = adapter

        calendarEventsListView.setOnItemClickListener { _, _, i, _ ->
            if (adapter!!.isSectionHeaderItem(i)) return@setOnItemClickListener

            val aco = adapter!!.getItem(i)
            val c = if (aco.isRange) aco.startCalendar else aco.calendar

            calendarView.setCurrentDate(c.time)

            val monthText = SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)
            val yearText = SimpleDateFormat("yyyy", Locale.getDefault()).format(c.time)
            supportActionBar?.title = "$monthText $yearText"
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(DATE_KEY, currDate)
    }

    private fun parseEvents() {
        val inputStream = resources.openRawResource(R.raw.academic_calendar)
        val byteArrayOutputStream = ByteArrayOutputStream()

        var ctr: Int
        try {
            ctr = inputStream.read()
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr)
                ctr = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            return
        }

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT+2")
            val jsonObject = JSONObject(byteArrayOutputStream.toString())
            val jsonArray = jsonObject.getJSONArray("events")

            for (i in 0 until jsonArray.length()) {
                val currentEvent = jsonArray.getJSONObject(i)

                val heName = currentEvent.getString("he_title")
                val enName = currentEvent.getString("en_title")
                val isRange = currentEvent.getBoolean("is_range")
                val isNormalHours = currentEvent.getBoolean("normal_class_hours")

                if (isRange) {
                    val startDate = currentEvent.getString("start_date")
                    val endDate = currentEvent.getString("end_date")

                    val endingDate = sdf.parse(endDate)
                    var startingDate = sdf.parse(startDate)

                    val endingCal = Calendar.getInstance()
                    endingCal.time = endingDate

                    while (startingDate.before(endingDate)) {
                        val c = Calendar.getInstance()
                        c.time = startingDate

                        val aco = AcademicCalendarObject(heName, enName, true, isNormalHours)
                        aco.startCalendar = c
                        aco.endCalendar = endingCal
                        adapter?.addSectionHeaderItem(aco)
                        adapter?.addItem(aco)

                        val event = Event(
                                ContextCompat.getColor(this,
                                        if (isNormalHours)
                                            R.color.green
                                        else R.color.google_red),
                                c.timeInMillis, aco)
                        calendarView.addEvent(event, true)
                        events.add(event)

                        startingDate = Date(startingDate.time + 1000 * 60 * 60 * 24)
                    }
                } else {
                    val date = currentEvent.getString("date")

                    val c = Calendar.getInstance()
                    c.time = sdf.parse(date)

                    val aco = AcademicCalendarObject(heName, enName, false, isNormalHours)
                    aco.calendar = c
                    adapter?.addSectionHeaderItem(aco)
                    adapter?.addItem(aco)

                    val event = Event(
                            ContextCompat.getColor(this,
                                    if (isNormalHours)
                                        R.color.green
                                    else R.color.google_red),
                            c.timeInMillis, aco)

                    calendarView.addEvent(event, true)
                    events.add(event)
                }
            }
        } catch (e: Exception) {
        }

        calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val events = calendarView.getEvents(dateClicked)
                if (events == null || events.size == 0) return

                val aco = events[0].data as AcademicCalendarObject
                calendarEventsListView.setSelection(adapter!!.getItemPosition(aco))

                currDate = dateClicked
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                val cal = Calendar.getInstance()
                cal.time = firstDayOfNewMonth
                val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
                val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(cal.time)
                supportActionBar?.title = "$monthName $year"
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
