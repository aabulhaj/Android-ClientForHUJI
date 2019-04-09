package com.aabulhaj.hujiapp.fragments

import Session
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.aabulhaj.hujiapp.Cache
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.MenuTint
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.activities.ChartActivity
import com.aabulhaj.hujiapp.activities.ExtraGradesActivity
import com.aabulhaj.hujiapp.activities.GPACalculatorActivity
import com.aabulhaj.hujiapp.adapters.CourseAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.*
import com.aabulhaj.hujiapp.util.PreferencesUtil
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call


class CoursesFragment : RefreshListFragment() {
    companion object {
        const val CACHE_FILENAME = "courses"
        const val YEARS_CACHE_FILENAME = "years"
    }

    private var currentYear: String? = null
    private var yearButton: MenuItem? = null
    private var coursesAdapter: CourseAdapter? = null
    private val allYears = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (currentYear == null) {
            currentYear = PreferencesUtil.getString(Session.getCacheKey("current_year"), "2019")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (coursesAdapter == null) {
            coursesAdapter = CourseAdapter(context!!)
            loadCache()
            onRefresh()
            listAdapter = coursesAdapter
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.courses_fragment_menu, menu)

        yearButton = menu.findItem(R.id.yearButton)
        yearButton?.title = currentYear

        MenuTint.tint(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.yearButton) {
            if (allYears.isNotEmpty()) {
                showYears()
            }
        } else if (item?.itemId == R.id.shnatonButton) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.enter_course_number)
            builder.setMessage(R.string.tip_shnaton)

            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                    resources.displayMetrics).toInt()

            val layout = LinearLayout(activity)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(padding, padding, padding, padding)

            val enterNumberTextView = TextView(context)
            enterNumberTextView.text = getString(R.string.enter_course_number)

            val courseNumberInput = EditText(context)
            courseNumberInput.inputType = InputType.TYPE_CLASS_NUMBER

            val enterYearTextView = TextView(context)
            enterYearTextView.text = getString(R.string.enter_year)

            val yearInput = EditText(context)
            yearInput.inputType = InputType.TYPE_CLASS_NUMBER
            yearInput.setText(currentYear)

            layout.addView(enterNumberTextView)
            layout.addView(courseNumberInput)
            layout.addView(enterYearTextView)
            layout.addView(yearInput)

            builder.setView(layout)

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val courseNumber = courseNumberInput.text.toString()
                val year = yearInput.text.toString()

                startChromeTab(getCourseShnatonURL(courseNumber, year), context!!)
            }
            builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }

            builder.show()
        } else if (item?.itemId == R.id.gpaCalcButton) {
            if (coursesAdapter?.count != 0) {
                val intent = Intent(activity, GPACalculatorActivity::class.java)
                intent.putExtra("courses", copyArray(coursesAdapter!!.getBackingArray()))
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun copyArray(arrayList: ArrayList<Grade>): ArrayList<Grade> {
        val copiedArray = ArrayList<Grade>()

        for (grade in arrayList) {
            val copiedGrade = Grade()

            copiedGrade.course = grade.course
            copiedGrade.extraGradesURL = grade.extraGradesURL
            copiedGrade.statisticsURL = grade.statisticsURL
            copiedGrade.grade = grade.grade
            copiedGrade.gradeType = grade.gradeType

            copiedArray.add(copiedGrade)
        }

        return copiedArray
    }

    private fun showYears() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.where_to_get_course))
        builder.setItems(allYears.toTypedArray()) { _, which ->
            currentYear = allYears[which]
            yearButton?.title = currentYear
            loadCache()
            PreferencesUtil.putString(Session.getCacheKey("current_year"), currentYear!!)
            onRefresh()
        }
        builder.show()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(coursesUrl(currentYear)),
                activity!!, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)

                // Set current year.
                val years = doc.getElementsByAttributeValue("name",
                        "yearsafa").first().getElementsByTag("option")

                allYears.clear()
                for (i in 0 until years.size) {
                    allYears.add(years[i].text())
                }

                if (years.isEmpty()) {
                    stopListRefreshing()
                    return
                }

                for (year in years) {
                    if (year.getElementsByAttribute("selected").isNotEmpty()) {
                        activity?.runOnUiThread {
                            yearButton?.title = allYears[years.indexOf(year)]
                            currentYear = allYears[years.indexOf(year)]
                            PreferencesUtil.putString(Session.getCacheKey("current_year"),
                                    currentYear ?: "")
                        }
                    }
                }

                Cache.cacheObject(context, allYears,
                        object : TypeToken<ArrayList<String>>() {}.type,
                        YEARS_CACHE_FILENAME)

                // Get courses.
                var sum = 0f
                var sumOfCredits = 0f
                val grades = ArrayList<Grade>()
                val table = doc.getElementsByAttributeValue("cellpadding", "2").first()
                        .select("tbody").first().select("tr").drop(2)

                val indexOfStatistics = 0
                val indexOfExtraGrade = 1
                val indexOfTypeOfGrade = 2
                val indexOfGrade = 3
                val indexOfCreditPoints = 4
                val indexOfCourseName = 5
                val indexOfCourseNubmer = 6

                for (row in table) {
                    val rowCols = row.select("td")
                    val grade = Grade()
                    grade.course = Course()

                    for (i in 0 until rowCols.size) {
                        val col = rowCols[i]
                        val colText = col.text()

                        when (i) {
                            indexOfStatistics -> {
                                val element = col.getElementsByTag("a").first()
                                grade.statisticsURL = element?.attr("href")
                            }
                            indexOfExtraGrade -> {
                                val element = col.children().first()
                                if (element?.attr("href") == null) {
                                    grade.extraGradesURL = null
                                } else {
                                    grade.extraGradesURL = Session.getHujiSessionUrl("/stu/") +
                                            element.attr("href")
                                }
                            }
                            indexOfTypeOfGrade -> {
                                grade.gradeType = CourseTypeEnum.getCourseTypeEnum(colText)
                            }
                            indexOfGrade -> {
                                if (colText != null && colText.isNotEmpty()) {
                                    if (colText == "עבר") {
                                        grade.grade = Grade.PASS
                                    } else if (colText == "נכשל") {
                                        grade.grade = Grade.FAIL
                                    } else if (colText == "פטור") {
                                        grade.grade = Grade.EXEMPT
                                    } else {
                                        try {
                                            grade.grade = if (colText.isNotEmpty())
                                                Integer.parseInt(colText)
                                            else -1
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                            indexOfCreditPoints -> {
                                if (colText == null || colText == ".00") {
                                    grade.course?.creditPoints = "0.00"
                                } else {
                                    grade.course?.creditPoints = colText
                                }
                            }
                            indexOfCourseName -> grade.course?.name = colText
                            indexOfCourseNubmer -> {
                                grade.course?.number = col.children().first().text()
                            }
                        }
                    }
                    if (grade.grade > 0 && grade.course!!.creditPoints!!.toFloat() > 0
                            && (grade.gradeType == CourseTypeEnum.FINAL
                                    || grade.gradeType == CourseTypeEnum.CALCULATED)) {
                        sum += grade.grade * grade.course?.creditPoints!!.toFloat()
                        sumOfCredits += grade.course?.creditPoints!!.toFloat()
                    }
                    grades.add(grade)
                }

                activity?.runOnUiThread {
                    yearButton?.title = currentYear
                    coursesAdapter?.clear()
                    if (grades.isNotEmpty()) {
                        coursesAdapter?.addAll(grades)
                    }
                    coursesAdapter?.notifyDataSetChanged()
                }
                Cache.cacheObject(activity, grades, object : TypeToken<ArrayList<Grade>>() {}.type,
                        CACHE_FILENAME + currentYear)
                stopListRefreshing()
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {
                stopListRefreshing()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.setSelector(android.R.color.transparent)
        listView.setOnItemLongClickListener { adapterView, _, i, _ ->
            val grade = adapterView.getItemAtPosition(i) as Grade
            shnatonOrSyllabusDialog(grade)
            return@setOnItemLongClickListener true
        }
    }

    private fun shnatonOrSyllabusDialog(grade: Grade) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(null)
        builder.setItems(arrayOf(resources.getString(R.string.shnaton),
                resources.getString(R.string.syllabus))) { _, which ->
            val url: String
            if (which == 0) {
                url = getCourseShnatonURL(grade.course!!.number!!, currentYear!!)
            } else {
                url = getCourseSyllabusURL(grade.course!!.number!!, currentYear!!)
            }
            startChromeTab(url, activity!!)
        }
        builder.show()
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

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        val grade = coursesAdapter!!.getItem(position)

        val options = ArrayList<String>()
        if (grade.statisticsURL != null) {
            options.add(getString(R.string.statistics))
        }
        if (grade.extraGradesURL != null) {
            options.add(getString(R.string.extra_marks))
        }

        showStatisticsAndExtraGradesDialog(grade, options.toTypedArray())
    }

    private fun showStatisticsAndExtraGradesDialog(grade: Grade, options: Array<String>) {
        if (options.isEmpty()) {
            return
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(null)
        builder.setItems(options) { _, which ->
            if (which == 0) {
                startStatisticsActivity(grade)
            } else if (which == 1) {
                startExtraGradesActivity(grade)
            }
        }
        builder.show()
    }

    private fun startStatisticsActivity(grade: Grade) {
        val details: Intent
        if (grade.statisticsURL != null) {
            details = Intent(activity, ChartActivity::class.java)
            details.putExtra("grade", grade)
            activity?.startActivity(details)
        }
    }

    private fun startExtraGradesActivity(grade: Grade) {
        val details: Intent
        if (grade.extraGradesURL != null) {
            details = Intent(activity, ExtraGradesActivity::class.java)
            details.putExtra("grade", grade)
            activity?.startActivity(details)
        }
    }

    private fun stopListRefreshing() {
        activity?.runOnUiThread {
            this.stopRefreshing()
        }
    }

    private fun loadCache() {
        val data = Cache.loadCachedObject(activity, object : TypeToken<ArrayList<Grade>>() {}.type,
                CACHE_FILENAME + currentYear) ?: return

        val yData = Cache.loadCachedObject(activity,
                object : TypeToken<ArrayList<String>>() {}.type, YEARS_CACHE_FILENAME) ?: return

        val grades = data as ArrayList<Grade>
        val yearsData = yData as ArrayList<String>

        activity?.runOnUiThread {
            coursesAdapter?.clear()
            coursesAdapter?.addAll(grades)
            coursesAdapter?.notifyDataSetChanged()

            allYears.clear()
            allYears.addAll(yearsData)
        }
    }
}
