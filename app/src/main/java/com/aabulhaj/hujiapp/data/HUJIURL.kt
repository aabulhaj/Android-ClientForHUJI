package com.aabulhaj.hujiapp.data

import Session
import java.util.*

const val HUJI_BASE_URL = "https://www.huji.ac.il"
const val HUJI_LOGIN_URL = "https://www.huji.ac.il/dataj/controller/stu/?"

fun timeTableUrl(semester: Int): String {
    return "/stu/STU-STULUACHSHAOT?winsub=yes&semester=$semester"
}

fun coursesUrl(year: String?): String {
    if (year == null) {
        return Session.getSessionUrl("/stu/STU-STUZIYUNIM?winsub=yes&safa=H")
    }
    return Session.getSessionUrl("/stu/STU-STUZIYUNIM?yearsafa=$year")
}

fun getCourseShnatonURL(courseNumber: String, year: String): String {
    return String.format(Locale.ENGLISH,
            "http://shnaton.huji.ac.il/index.php?peula=Simple&course=%s&year=%s",
            courseNumber,
            year)
}

fun getCourseSyllabusURL(courseNumber: String, year: String): String {
    return String.format(Locale.ENGLISH, "http://shnaton.huji.ac.il/index.php/NewSyl/%s/1/%s/",
            courseNumber, year)
}

fun getStatisticsUrl(statisticsUrl: String): String {
    return Session.getSessionUrl("/stu/$statisticsUrl")
}

fun getExamURL(): String {
    return Session.getSessionUrl("/stu/STU-STULUACHBCHINOT?yearno=2019")
}

fun getNoteBooksURL(): String {
    return Session.getSessionUrl("/stu/STU-STUNOTEBOOKSSTART")
}

fun getNoteBooksURL(url: String): String {
    return Session.getSessionUrl("/stu/$url")
}

fun getAboutMeURL(): String {
    return Session.getSessionUrl("/stu/STU-UPDATEMOREFORM")
}

fun getShnatonExamLinkForCourse(number: String, year: Int): String {
    return String.format(Locale.ENGLISH,
            "http://shnaton.huji.ac.il/index.php?peula=CourseD&course=%s&detail=examDates&year=%d",
            number, year)
}