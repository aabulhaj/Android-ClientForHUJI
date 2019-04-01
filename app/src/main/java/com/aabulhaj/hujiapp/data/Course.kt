package com.aabulhaj.hujiapp.data

import java.io.Serializable

class Course : Serializable {
    var number: String? = null
    var name: String? = null
    var creditPoints: String? = null
    var year: Int? = null

    // Indicates "like above" in the timetable.
    var aboveClass: Boolean = false

    constructor()

    constructor(aboveClass: Boolean) {
        this.aboveClass = aboveClass
    }

    constructor(name: String?, number: String?) {
        this.name = name
        this.number = number
    }

    fun getCourseNumber(): String {
        return number!!
    }

    fun isEmptyClass(): Boolean {
        return name == null && number == null
    }
}