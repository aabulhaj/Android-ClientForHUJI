package com.aabulhaj.hujiapp.activities

import Session
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.AttrRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Grade
import com.aabulhaj.hujiapp.data.getStatisticsUrl
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.android.synthetic.main.activity_chart.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import java.util.*


class ChartActivity : ToolbarActivity() {
    private var yVals = ArrayList<BarEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        setTextViewsVisibility(View.GONE)

        val averages = arrayOfNulls<String>(19)
        for (i in 0..18) {
            averages[i] = ((i + 1) * 5).toString()
        }

        chart.setDrawValueAboveBar(true)
        chart.isClickable = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisLeft.setAxisMinValue(0f)
        chart.axisRight.setAxisMinValue(0f)
        chart.setDescription("")
        chart.isHighlightPerDragEnabled = false
        chart.setPinchZoom(false)
        chart.setDoubleTapToZoomEnabled(false)
        chart.setNoDataText(getString(R.string.loading_data))
        chart.getPaint(BarChart.PAINT_INFO).color = ContextCompat.getColor(this, R.color.colorAccent)
        chart.axisLeft.textColor = getColorFromAttr(android.R.attr.textColor)
        chart.xAxis.textColor = getColorFromAttr(android.R.attr.textColor)
        chart.legend.textColor = getColorFromAttr(android.R.attr.textColor)
        chart.legend.isEnabled = false
        chart.xAxis.textSize = 9f
        chart.axisRight.isEnabled = false
        chart.axisLeft.valueFormatter = (object : YAxisValueFormatter {
            override fun getFormattedValue(value: Float, yaxis: YAxis): String {
                return String.format(Locale.US, "%d", value.toInt())
            }
        })

        val grade = intent.getSerializableExtra("grade") as Grade
        title = grade.course?.name

        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(
                getStatisticsUrl(grade.statisticsURL!!)),
                this, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)
                val elements = doc.getElementsByTag("center")

                var graphLink = elements.first().getElementsByTag("img").first().attr("src")
                graphLink = java.net.URLDecoder.decode(graphLink, "UTF-8")
                graphLink = graphLink.substring(graphLink.indexOf("?") + 1)

                val components = graphLink.split("&")
                val dataPoints = ArrayList<DataPoint>()

                for (c in components) {
                    val component = c.split("=")

                    if ("data" == component[0]) {
                        val values = component[1].split(",")
                        for (value in values) {
                            val dataPoint = value.split(";")
                            val gradeRange = dataPoint[1].split("-".toRegex())
                            dataPoints.add(DataPoint(Integer.valueOf(gradeRange[0]),
                                    Integer.valueOf(gradeRange[1]), Integer.valueOf(dataPoint[0])))
                        }
                    }
                }

                yVals = ArrayList()
                val xVals = ArrayList<String>()
                val colors = IntArray(dataPoints.size)
                for (i in 0 until dataPoints.size) {
                    xVals.add(String.format(Locale.getDefault(), "%d - %d",
                            dataPoints[i].lowBound,
                            dataPoints[i].highBound))
                    yVals.add(BarEntry(dataPoints[i].count.toFloat(), i))
                    if (dataPoints[i].isGradeInDatePoint(grade.grade)) {
                        colors[i] = Color.rgb(52, 152, 219)
                    } else {
                        colors[i] = Color.rgb(38, 165, 148)
                    }
                }

                val dataSet = BarDataSet(yVals, "")
                dataSet.isHighlightEnabled = false
                dataSet.valueFormatter = object : ValueFormatter {
                    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int,
                                                   viewPortHandler: ViewPortHandler): String {
                        return String.format(Locale.US, "%d", value.toInt())
                    }
                }
                dataSet.setColors(colors)
                dataSet.valueTextColor = getColorFromAttr(android.R.attr.textColor)

                runOnUiThread {
                    val data = BarData(xVals, dataSet)
                    if (chart != null) {
                        chart.data = data
                        chart.animateY(1000, Easing.EasingOption.EaseInOutQuad)
                    }
                    setTextViewsVisibility(View.VISIBLE)
                    setStatistics(60)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {
                runOnUiThread { chart.setNoDataText(e.message) }
            }
        })

        averageTextView.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(null)
            builder.setItems(averages) { _, which ->
                setStatistics((which + 1) * 5)
            }
            builder.show()
        }
    }

    private fun Context.getColorFromAttr(
            @AttrRes attrColor: Int,
            typedValue: TypedValue = TypedValue(),
            resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    private inner class DataPoint(var lowBound: Int,
                                  var highBound: Int,
                                  var count: Int) {

        fun isGradeInDatePoint(grade: Int): Boolean {
            return grade >= 0 && grade >= lowBound && grade <= highBound
        }
    }

    private fun setTextViewsVisibility(visibility: Int) {
        ifPassingGradeTextView.visibility = visibility
        statisticsTextView.visibility = visibility
        averageTextView.visibility = visibility
    }

    private fun setStatistics(grade: Int) {
        averageTextView.text = grade.toString()

        var totalStudents = 0
        var numberOfFailures = 0
        var numberOfPasses = 0
        var passingPercentage = 100.00f
        var failingPercentage = 0.00f

        for (numberOfStudents in yVals) {
            totalStudents += numberOfStudents.getVal().toInt()
        }

        if (totalStudents > 0) {
            for (i in 0..(grade - 50) / 5) {
                numberOfFailures += yVals[i].getVal().toInt()
            }
            numberOfPasses = totalStudents - numberOfFailures
            failingPercentage = numberOfFailures.toFloat() / totalStudents * 100.00f
            passingPercentage = 100.00f - failingPercentage
        }

        statisticsTextView.text = getString(R.string.students_passed, numberOfPasses,
                passingPercentage, numberOfFailures, failingPercentage)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}
