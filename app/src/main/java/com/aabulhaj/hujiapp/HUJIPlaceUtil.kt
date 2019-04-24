package com.aabulhaj.hujiapp

import android.content.Context
import com.aabulhaj.hujiapp.data.HUJIPlace
import com.aabulhaj.hujiapp.data.HUJIPlace.PlaceType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


object HUJIPlaceUtil {
    private var latIndex: Int = 0
    private var lngIndex: Int = 0
    private var svlatIndex: Int = 0
    private var svlngIndex: Int = 0
    private var headingIndex: Int = 0
    private var pitchIndex: Int = 0
    private var typeIndex: Int = 0
    private var enNameIndex: Int = 0
    private var openingHoursIndex: Int = 0

    fun getPlaceFromName(place: String, context: Context?): HUJIPlace? {
        val csvStream = context?.resources?.openRawResource(R.raw.huji_places)
        try {
            val reader = BufferedReader(InputStreamReader(csvStream))

            var hebrewShortNameIndex = 0

            var line = reader.readLine()
            val firstLineData = line.split(",").toTypedArray()
            for (i in firstLineData.indices) {
                when (firstLineData[i]) {
                    "he-short" -> hebrewShortNameIndex = i
                    "lat" -> latIndex = i
                    "lon" -> lngIndex = i
                    "svlat" -> svlatIndex = i
                    "svlon" -> svlngIndex = i
                    "heading" -> headingIndex = i
                    "pitch" -> pitchIndex = i
                    "type" -> typeIndex = i
                }
            }
            val placeSplit = place.split(" ").toTypedArray()
            if (placeSplit[0] == "חבר") {
                placeSplit[0] = "חברה"
            }

            line = reader.readLine()
            while (line != null) {
                val rowData = line.split(",").toTypedArray()
                if (placeSplit[0] == "רוח" || placeSplit[0] == "חברה") {
                    if (placeSplit[0] + " " + placeSplit[1][0] == rowData[hebrewShortNameIndex]) {
                        return hujiPlaceFromRowData(place, rowData)
                    }
                } else if (placeSplit[0].startsWith("בויאר")
                        && "בויאר" == rowData[hebrewShortNameIndex]) {
                    return hujiPlaceFromRowData(place, rowData)
                } else if (placeSplit[0] == rowData[hebrewShortNameIndex]) {
                    return hujiPlaceFromRowData(place, rowData)
                }
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                csvStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    private fun hujiPlaceFromRowData(place: String, rowData: Array<String>): HUJIPlace {
        val lat = rowData[latIndex].toDouble()
        val lon = rowData[lngIndex].toDouble()
        val heading = rowData[headingIndex].toDouble()
        val pitch = rowData[pitchIndex].toDouble()
        val svlat = rowData[svlatIndex].toDouble()
        val svlng = rowData[svlngIndex].toDouble()
        val type = rowData[typeIndex].toInt()

        if (rowData.size > openingHoursIndex) {
            val openingHours = rowData[openingHoursIndex]
            return HUJIPlace(place, PlaceType.fromInt(type), lat, lon, svlat, svlng, heading, pitch,
                    openingHours)
        }
        return HUJIPlace(place, PlaceType.fromInt(type), lat, lon, svlat, svlng, heading, pitch)
    }

    fun getPlacesOnCampus(campus: Int, context: Context): ArrayList<HUJIPlace> {
        val places = ArrayList<HUJIPlace>()

        val csvStream = context.resources.openRawResource(campus)
        try {
            val reader = BufferedReader(InputStreamReader(csvStream))

            var line = reader.readLine()
            val firstLineData = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in firstLineData.indices) {
                when (firstLineData[i]) {
                    "lat" -> latIndex = i
                    "lon" -> lngIndex = i
                    "svlat" -> svlatIndex = i
                    "svlon" -> svlngIndex = i
                    "heading" -> headingIndex = i
                    "pitch" -> pitchIndex = i
                    "type" -> typeIndex = i
                    "en-name" -> enNameIndex = i
                    "opening_hours" -> openingHoursIndex = i
                }
            }

            line = reader.readLine()
            while (line != null) {
                val rowData = line.split(",").toTypedArray()
                places.add(hujiPlaceFromRowData(rowData[enNameIndex], rowData))
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                csvStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return places
    }

    fun getHUJIPlaceTypeIconID(hujiPlace: HUJIPlace): String {
        return when (hujiPlace.type) {
            HUJIPlace.PlaceType.NORMAL -> "\uD83C\uDFE2"
            PlaceType.COMPUTER_LAB -> "\uD83D\uDCBB"
            HUJIPlace.PlaceType.LIBRARY -> "\uD83D\uDCDA"
            PlaceType.RESTAURANT -> "\uD83C\uDF74"
            HUJIPlace.PlaceType.CAFE -> "\u2615"
            HUJIPlace.PlaceType.SPORT -> "\uD83D\uDCAA"
        }
    }

    fun getTodayHours(hours: String, context: Context): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        val hoursOnDifferentDays = hours.split(";")
        for (daysPart in hoursOnDifferentDays) {
            val daysPartSegments = daysPart.split(" ")

            val daysRange = daysPartSegments[0].split("-")

            if (daysRange.size == 1) {
                if (day == daysRange[0].substring(0, daysRange[0].length - 1).toInt())
                    return daysPartSegments[1]
            } else {
                val startingInt = daysRange[0].toInt()
                val end = daysRange[1].substring(0, daysRange[1].length - 1).toInt()

                if (day <= end && day >= startingInt) {
                    return daysPartSegments[1]
                }
            }
        }
        return context.getString(R.string.closed)
    }

    fun isHUJIPlaceOpen(hours: String): Boolean {
        val hoursOnDifferentDays = hours.split(";")
        for (daysPart in hoursOnDifferentDays) {
            val daysPartSegments = daysPart.split(" ")

            val daysRange: List<String>
            val hoursRange: List<String>
            try {
                daysRange = daysPartSegments[0].split("-")
                hoursRange = daysPartSegments[1].split("-")
            } catch (e: Exception) {
                return false
            }

            var opensToday = false
            if (daysRange.size == 1) {
                opensToday = isItOpenToday(daysRange[0].substring(0, daysRange[0].length - 1))
            } else {
                val startingInt = daysRange[0].toInt()
                val end = daysRange[1].substring(0, daysRange[1].length - 1).toInt()
                for (i in startingInt..end) {
                    opensToday = isItOpenToday(daysRange[0])
                    if (opensToday)
                        break
                }
            }
            if (opensToday && isInOpeningHours(hoursRange.toTypedArray())) {
                return true
            }
        }
        return false
    }

    private fun isInOpeningHours(openingHours: Array<String>): Boolean {
        val cal = Calendar.getInstance()
        val currentLocalTime = cal.time
        val date = SimpleDateFormat("HH:mm", Locale.getDefault())
        val hourAndMinutes = date.format(currentLocalTime).trim().split(":")

        val sysHour = Integer.valueOf(hourAndMinutes[0])
        val sysMinutes = Integer.valueOf(hourAndMinutes[1])

        val startingHourAndMin = openingHours[0].split(":")
        val closingHourAndMin = openingHours[1].split(":")

        val startingHour = Integer.valueOf(startingHourAndMin[0])
        val startingMinutes = Integer.valueOf(startingHourAndMin[1])

        val closingHour = Integer.valueOf(closingHourAndMin[0])
        val closingMinutes = Integer.valueOf(closingHourAndMin[1])

        if (sysHour < startingHour || sysHour > closingHour)
            return false

        if (sysHour == startingHour && sysMinutes < startingMinutes)
            return false

        return !(sysHour == closingHour && sysMinutes > closingMinutes)

    }

    private fun isItOpenToday(reqDay: String): Boolean {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        return day == reqDay.toInt()
    }
}