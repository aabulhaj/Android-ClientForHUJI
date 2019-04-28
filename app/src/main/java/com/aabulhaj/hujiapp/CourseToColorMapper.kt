package com.aabulhaj.hujiapp

import android.content.Context
import com.aabulhaj.hujiapp.data.Course
import com.google.gson.reflect.TypeToken


private const val MAP_CACHE_NAME = "mapper_cache"

object CourseToColorMapper {
    private var colorsMap = HashMap<String, Int>()

    fun getColor(course: Course): Int {
        if (!colorsMap.containsKey(course.number)) {
            colorsMap[course.getCourseNumber()] = randomFlatColor()
        }

        return colorsMap[course.getCourseNumber()]!!
    }

    fun setColor(course: Course, color: Int, context: Context) {
        colorsMap[course.getCourseNumber()] = color
        cacheMap(context)
    }

    fun cacheMap(context: Context?) {
        Cache.cacheObject(
                context,
                colorsMap,
                object : TypeToken<HashMap<String, Int>>() {}.type,
                MAP_CACHE_NAME)
    }

    fun loadCachedMap(context: Context?) {
        if (colorsMap.isNotEmpty()) {
            return
        }
        val cacheMap = Cache.loadCachedObject(
                context,
                object : TypeToken<HashMap<String, Int>>() {}.type,
                MAP_CACHE_NAME) ?: return
        colorsMap = cacheMap as HashMap<String, Int>
    }
}