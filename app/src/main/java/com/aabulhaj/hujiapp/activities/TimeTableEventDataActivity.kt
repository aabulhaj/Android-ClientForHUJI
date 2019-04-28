package com.aabulhaj.hujiapp.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.TimeTableClass
import com.aabulhaj.hujiapp.data.getCourseShnatonURLWithoutYear
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_time_table_event_data.*
import java.text.SimpleDateFormat
import java.util.*


class TimeTableEventDataActivity : BaseMapActivity(), View.OnClickListener {
    private lateinit var timeTableClass: TimeTableClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_event_data)

        timeTableClass = intent.extras.getSerializable("time_Table_class") as TimeTableClass

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = timeTableClass.course.name

        tableEventCourseNumber.text = timeTableClass.course.number
        tableEventCourseNumber.setOnClickListener(this)

        tableEventCourseName.text = timeTableClass.course.name

        tableEventTime.text = getDate(timeTableClass.startTime.timeInMillis, "hh:mm") + "-" + getDate(timeTableClass.endTime.timeInMillis, "hh:mm")
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.tableEventCourseNumber) {
            val url = getCourseShnatonURLWithoutYear(timeTableClass.course.number!!)
            startChromeTab(url, this)
        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        if (!timeTableClass.classLocation.isNullOrEmpty()) {
            setLocationByName(timeTableClass.classLocation!!)
        }
    }

    private fun startChromeTab(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val extras = Bundle()
        extras.putBinder("android.support.customtabs.extra.SESSION", null)
        val color = context.resources.getColor(R.color.colorPrimary, context.theme)

        extras.putInt("android.support.customtabs.extra.TOOLBAR_COLOR", color)
        intent.putExtras(extras)

        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            intent.setPackage(null)
            context.startActivity(intent)
        }

    }
}
