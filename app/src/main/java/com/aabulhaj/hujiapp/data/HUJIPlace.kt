package com.aabulhaj.hujiapp.data

import android.content.Context
import com.aabulhaj.hujiapp.R
import com.google.android.gms.maps.model.LatLng
import java.util.*

class HUJIPlace : Comparable<HUJIPlace> {

    var coordinate: LatLng
    var heading: Double = 0.toDouble()
    var pitch: Double = 0.toDouble()
    var helpString: String? = null
    var name: String? = null
    var type: PlaceType
    var streetViewCoordinate: LatLng? = null
    var openingHours: String? = null

    enum class PlaceType constructor(val type: Int) {
        NORMAL(0),
        COMPUTER_LAB(1),
        LIBRARY(2),
        RESTAURANT(3),
        CAFE(4),
        SPORT(5);

        fun getValue(): Int {
            return type
        }

        fun getText(context: Context): String {
            return when (type) {
                0 -> context.getString(R.string.building)
                1 -> context.getString(R.string.computer_lab)
                2 -> context.getString(R.string.library)
                3 -> context.getString(R.string.resturant)
                4 -> context.getString(R.string.cafe)
                5 -> context.getString(R.string.sport_center)
                else -> ""
            }
        }

        companion object {

            private val intToTypeMap = HashMap<Int, PlaceType>()

            init {
                for (type in PlaceType.values()) {
                    intToTypeMap[type.getValue()] = type
                }
            }

            fun fromInt(i: Int): PlaceType {
                return intToTypeMap[i]!!
            }
        }
    }

    override fun compareTo(other: HUJIPlace): Int {
        return this.name!!.compareTo(other.name!!)
    }

    constructor(lat: Double, lon: Double, heading: Double, pitch: Double)
            : this(null, lat, lon, heading, pitch)

    constructor(name: String?, lat: Double, lon: Double, heading: Double, pitch: Double)
            : this(name, PlaceType.NORMAL, lat, lon, -1.0, -1.0, heading, pitch)

    constructor(name: String?, type: PlaceType, lat: Double, lon: Double, svLat: Double,
                svLon: Double, heading: Double, pitch: Double) {
        this.name = name
        this.type = type
        this.coordinate = LatLng(lat, lon)
        if (svLat != -1.0 && svLon != -1.0)
            this.streetViewCoordinate = LatLng(svLat, svLon)
        this.heading = heading
        this.pitch = pitch
    }

    constructor(name: String, type: PlaceType, lat: Double, lon: Double, svLat: Double,
                svLon: Double, heading: Double, pitch: Double, openingHours: String) {
        this.name = name
        this.type = type
        this.coordinate = LatLng(lat, lon)
        if (svLat != -1.0 && svLon != -1.0)
            this.streetViewCoordinate = LatLng(svLat, svLon)
        this.heading = heading
        this.pitch = pitch
        this.openingHours = openingHours
    }
}
