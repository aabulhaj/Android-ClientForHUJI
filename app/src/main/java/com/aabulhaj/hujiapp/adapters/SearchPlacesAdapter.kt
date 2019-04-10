package com.aabulhaj.hujiapp.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.aabulhaj.hujiapp.HUJIPlaceUtil
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.HUJIPlace
import de.halfbit.pinnedsection.PinnedSectionListView
import kotlinx.android.synthetic.main.search_places_row.view.*
import java.util.*


private const val TYPE_ITEM = 0
private const val TYPE_SEPARATOR = 1

class SearchPlacesAdapter(private val context: Context) : BaseAdapter(),
        PinnedSectionListView.PinnedSectionListAdapter {

    private val data = ArrayList<HUJIPlace>()
    private val sectionHeader = TreeSet<Int>()

    private var longitude = 35.2375756
    private var latitude = 31.8117442
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        val hasPerm = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            (context as Activity).requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            updateLocation()
        }
    }

    private fun updateLocation() {
        val hasPerm = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            return
        }

        try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider)
                if (l != null && (bestLocation == null || l.accuracy < bestLocation.accuracy)) {
                    bestLocation = l
                }
            }
            if (bestLocation == null)
                return
            longitude = bestLocation.longitude
            latitude = bestLocation.latitude
        } catch (e: Exception) {
        }

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var v = convertView
        val holder: ViewHolder
        val rowType = getItemViewType(position)

        if (v == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    v = inflater.inflate(R.layout.search_places_row, parent, false) as View
                    holder.buildingNameTextView = v.placeNameTextView
                    holder.distanceTextView = v.numberOfKm
                    holder.unitOfDistanceTextView = v.distanceUnitTextView
                    holder.buildingTypeTextView = v.buildingTypeTextView
                    holder.buildingIconTextView = v.buildingTextView
                    holder.hoursTextView = v.hoursTextView
                    holder.openClosedTextView = v.openClosed
                    holder.dotSeperator = v.dotSeparatorTextView
                }
                TYPE_SEPARATOR -> {
                    v = inflater.inflate(R.layout.header_row, parent, false)
                    holder.seperatorTextView = v.findViewById(android.R.id.text1)
                }
            }
            v?.tag = holder
        } else {
            holder = v.tag as ViewHolder
        }

        val hujiPlace = getItem(position)

        if (rowType == TYPE_SEPARATOR) {
            holder.seperatorTextView?.text = hujiPlace.name
        } else if (rowType == TYPE_ITEM) {
            holder.buildingNameTextView?.text = hujiPlace.name

            val results = FloatArray(3)
            Location.distanceBetween(hujiPlace.coordinate.latitude, hujiPlace.coordinate.longitude,
                    latitude, longitude, results)
            val distanceInMeters = results[0]
            var distanceInKiloMeters = -1f
            if (distanceInMeters > 1000) {
                distanceInKiloMeters = distanceInMeters / 1000
            }

            holder.distanceTextView?.text = String.format(Locale.getDefault(), "%.2f",
                    if (distanceInKiloMeters == -1f)
                        distanceInMeters
                    else distanceInKiloMeters)
            holder.unitOfDistanceTextView?.text =
                    if (distanceInKiloMeters == -1f)
                        context.getString(R.string.meters)
                    else context.getString(R.string.kilo_meters)

            holder.buildingTypeTextView?.text = hujiPlace.type.getText(context)

            holder.buildingIconTextView?.text = HUJIPlaceUtil.getHUJIPlaceTypeIconID(hujiPlace)


            holder.openClosedTextView?.visibility = View.VISIBLE
            holder.dotSeperator?.visibility = View.VISIBLE
            val hours = hujiPlace.openingHours
            if (hours == null || hours.trim { it <= ' ' } == "" || hours.trim { it <= ' ' } == "-1") {
                holder.hoursTextView?.text = ""
                holder.openClosedTextView?.visibility = View.GONE
                holder.dotSeperator?.visibility = View.GONE
            } else if (hours.trim { it <= ' ' } == "24/7") {
                holder.hoursTextView?.text = context.getString(R.string.opentwentyfour)
                holder.openClosedTextView?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.open,
                        0, 0, 0)
                holder.dotSeperator?.text = "\u2022"
            } else {
                holder.dotSeperator?.text = "\u2022"
                val isOpen = HUJIPlaceUtil.isHUJIPlaceOpen(hours)
                holder.openClosedTextView?.setCompoundDrawablesWithIntrinsicBounds(
                        if (isOpen)
                            R.drawable.open
                        else R.drawable.closed,
                        0, 0, 0)

                val todayHours = HUJIPlaceUtil.getTodayHours(hours, context)
                holder.hoursTextView?.text = todayHours
            }
        }
        return v
    }

    private class ViewHolder {
        internal var distanceTextView: TextView? = null
        internal var unitOfDistanceTextView: TextView? = null
        internal var buildingNameTextView: TextView? = null
        internal var seperatorTextView: TextView? = null

        internal var buildingTypeTextView: TextView? = null
        internal var buildingIconTextView: TextView? = null
        internal var openClosedTextView: TextView? = null
        internal var hoursTextView: TextView? = null
        internal var dotSeperator: TextView? = null
    }

    fun addItem(item: HUJIPlace) {
        data.add(item)
        notifyDataSetChanged()
    }

    fun addAll(items: List<HUJIPlace>) {
        for (item in items) {
            addItem(item)
        }
        notifyDataSetChanged()
    }

    fun addSectionHeaderItem(item: HUJIPlace) {
        sectionHeader.add(data.size)
        data.add(item)
        notifyDataSetChanged()
    }

    fun removeSectionHeaderItem(item: HUJIPlace) {
        data.remove(item)
        sectionHeader.remove(data.size)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): HUJIPlace {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun clear() {
        data.clear()
        sectionHeader.clear()
        notifyDataSetChanged()
    }

}
