package com.aabulhaj.hujiapp.data

import java.util.*

class AcademicCalendarObject(var heName: String, var enName: String,
                             var isRange: Boolean, var isNormalHours: Boolean) {
    var startCalendar = Calendar.getInstance()
    var endCalendar = Calendar.getInstance()
    var calendar = Calendar.getInstance()
}
