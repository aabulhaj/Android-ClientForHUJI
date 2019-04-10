package com.aabulhaj.hujiapp.fragments

import Session
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.*
import com.aabulhaj.hujiapp.HUJIPlaceUtil
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.activities.PLACES_CHECKED_POS_TAGS
import com.aabulhaj.hujiapp.activities.PlacesDialogActivity
import com.aabulhaj.hujiapp.activities.SearchPlacesActivity
import com.aabulhaj.hujiapp.data.HUJIPlace
import com.aabulhaj.hujiapp.util.PreferencesUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*

private const val CATEGORIES_CODE = 545
private const val TAB_KEY = "tab_map_sel"

class MapFragment : BaseMapFragment(), RefreshableFragment {
    private val allPlaces = ArrayList<HUJIPlace>()

    private var checkedTab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val scopus = HUJIPlaceUtil.getPlacesOnCampus(R.raw.scopus, activity!!)
        val edmond = HUJIPlaceUtil.getPlacesOnCampus(R.raw.edmond, activity!!)

        scopus.sort()
        edmond.sort()

        allPlaces.addAll(scopus)
        allPlaces.addAll(edmond)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)
        super.setView(v)
        super.onCreateView(inflater, container, savedInstanceState)

        v.mapTabLayout.addTab(v.mapTabLayout.newTab().setText(getString(R.string.emond_safra)))
        v.mapTabLayout.addTab(v.mapTabLayout.newTab().setText(getString(R.string.mount_scopus)))

        v.mapTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    PreferencesUtil.putInt(Session.getCacheKey(TAB_KEY), 0)
                    checkedTab = 0
                    handleEdmondSafra(true)
                } else {
                    PreferencesUtil.putInt(Session.getCacheKey(TAB_KEY), 1)
                    checkedTab = 1
                    handleMountScopus(true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    PreferencesUtil.putInt(Session.getCacheKey(TAB_KEY), 0)
                    checkedTab = 0
                    handleEdmondSafra(true)
                } else {
                    PreferencesUtil.putInt(Session.getCacheKey(TAB_KEY), 1)
                    checkedTab = 1
                    handleMountScopus(true)
                }
            }
        })

        return v
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.mapCategories) {
            startActivityForResult(Intent(activity, PlacesDialogActivity::class.java),
                    CATEGORIES_CODE)
            return true
        } else if (item?.itemId == R.id.mapPlacesMenuButton) {
            startActivity(Intent(activity, SearchPlacesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.map_fragment_menu, menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CATEGORIES_CODE && resultCode == Activity.RESULT_OK) {
            setOnMap(data)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        setOnMap(null)
        checkedTab = PreferencesUtil.getInt(Session.getCacheKey(TAB_KEY), 0)

        mapTabLayout?.getTabAt(checkedTab)?.select()

        if (checkedTab == 0) {
            handleEdmondSafra(false)
        } else {
            handleMountScopus(false)
        }
    }

    private fun handleMountScopus(animate: Boolean) {
        if (animate) {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(31.7931538, 35.2436038), 15f))
        } else {
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(31.7931538, 35.2436038), 15f))
        }
    }

    private fun handleEdmondSafra(animate: Boolean) {
        if (animate) {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(31.774346, 35.198012), 15f))
        } else {
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(31.774346, 35.198012), 15f))
        }

    }

    private fun setOnMap(data: Intent?) {
        val places: BooleanArray?
        if (data != null) {
            places = data.getBooleanArrayExtra(PLACES_CHECKED_POS_TAGS)
        } else {
            places = PlacesDialogActivity.getCachedArray(activity!!)
        }

        googleMap?.clear()
        if (places != null) {
            for (place in HUJIPlaceUtil.getPlacesOnCampus(R.raw.huji_places, activity!!)) {
                if (places[place.type.getValue()]) {
                    addPlaceToMapWithColor(place, false, false)
                }
            }
        }
    }

    private fun setHUJIPlaceOnMap(hujiPlace: String) {
        for (place in HUJIPlaceUtil.getPlacesOnCampus(R.raw.huji_places, activity!!)) {
            if (place.name!!.trim { it <= ' ' }.toLowerCase()
                    == hujiPlace.trim { it <= ' ' }.toLowerCase()) {
                addPlaceToMapWithColor(place, true, true)
                return
            }
        }
    }


    override fun refresh() {

    }

    override fun getFragment(): Fragment {
        return this
    }
}
