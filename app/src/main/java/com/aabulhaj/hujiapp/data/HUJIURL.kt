package com.aabulhaj.hujiapp.data

import Session

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
