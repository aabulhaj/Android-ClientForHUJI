package com.aabulhaj.hujiapp.fragments

import Session
import android.os.Bundle
import android.view.*
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.MenuTint
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.CourseAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.Course
import com.aabulhaj.hujiapp.data.Grade
import com.aabulhaj.hujiapp.data.coursesUrl
import com.aabulhaj.hujiapp.util.PreferencesUtil
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call


class CoursesFragment : RefreshListFragment() {
    private var currentYear: String? = null
    private var yearButton: MenuItem? = null
    private var coursesAdapter: CourseAdapter? = null
    private val allYears = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (coursesAdapter == null) {
            coursesAdapter = CourseAdapter(context!!)
            listAdapter = coursesAdapter
        }

        onRefresh()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.courses_fragment_menu, menu)

        if (currentYear == null) {
            currentYear = PreferencesUtil.getString(Session.getCacheKey("current_year"), "2019")
        }

        yearButton = menu.findItem(R.id.yearButton)
        yearButton?.title = currentYear

        MenuTint.tint(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        Session.callRequest(fun() = Session.hujiApiClient.getCoursesList(coursesUrl(currentYear)),
                activity!!, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)

                // Set current year.
                val years = doc.getElementsByAttributeValue("name",
                        "yearsafa").first().getElementsByTag("option")

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
                    coursesAdapter?.clear()
                    if (grades.isNotEmpty()) {
                        coursesAdapter?.addAll(grades)
                    }
                    coursesAdapter?.notifyDataSetChanged()
                }
                stopListRefreshing()
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {
                stopListRefreshing()
            }
        })
    }

    private fun stopListRefreshing() {
        activity?.runOnUiThread {
            this.stopRefreshing()
        }
    }
}
