package com.aabulhaj.hujiapp.data

const val HUJI_BASE_URL = "https://www.huji.ac.il"
const val HUJI_LOGIN_URL = "https://www.huji.ac.il/dataj/controller/stu/?"

fun timeTableUrl(semester: Int): String {
    return "/stu/STU-STULUACHSHAOT?winsub=yes&semester=$semester"
}
