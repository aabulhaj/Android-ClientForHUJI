package com.aabulhaj.hujiapp

import android.content.Context


enum class CourseTypeEnum {
    FINAL,
    MIDTERM,
    PARTONE,
    PARTTWO,
    PARTTHREE,
    PARTFOUR,
    PARTIAL,
    CALCULATED,
    EXPECTED,
    UNKNOWN;


    companion object {

        fun getString(courseEnum: CourseTypeEnum?, context: Context): String {
            if (courseEnum == null) {
                return ""
            }
            when (courseEnum) {
                FINAL -> return context.getString(R.string.final_exam)
                MIDTERM -> return context.getString(R.string.mid_term)
                PARTONE -> return context.getString(R.string.part, 1)
                PARTTWO -> return context.getString(R.string.part, 2)
                PARTTHREE -> return context.getString(R.string.part, 3)
                PARTFOUR -> return context.getString(R.string.part, 4)
                PARTIAL -> return context.getString(R.string.partial)
                CALCULATED -> return context.getString(R.string.calculated)
                UNKNOWN -> return context.getString(R.string.unknown)
                EXPECTED -> return context.getString(R.string.expected)
            }
        }

        fun getActualString(courseTypeEnum: CourseTypeEnum, context: Context): String {
            when (courseTypeEnum) {
                FINAL -> return "סופי"
                MIDTERM -> return "אמצע"
                PARTONE -> return "חלקי1"
                PARTTWO -> return "חלקי2"
                PARTTHREE -> return "חלקי3"
                PARTFOUR -> return "חלקי4"
                PARTIAL -> return "חלקי"
                CALCULATED -> return "מחושב"
                EXPECTED -> return "צפוי"
                UNKNOWN -> return context.getString(R.string.unknown)
            }
        }

        fun getCourseTypeEnum(type: String): CourseTypeEnum {
            when (type) {
                "סופי" -> return FINAL
                "סופית" -> return FINAL
                "מחושב" -> return CALCULATED
                "חלקי" -> return PARTIAL
                "אמצע" -> return MIDTERM
                "צפוי" -> return EXPECTED
            }

            var part = -1
            if (type.startsWith("חלקית")) {
                part = Integer.valueOf(
                        type.split(" ".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()[1])
            } else if (type.startsWith("חלקי")) {
                part = Integer.valueOf(type.replace("חלקי", ""))
            }

            return if (part == 1)
                PARTONE
            else if (part == 2)
                PARTTWO
            else if (part == 3)
                PARTTHREE
            else if (part == 4)
                PARTFOUR
            else
                UNKNOWN
        }
    }
}