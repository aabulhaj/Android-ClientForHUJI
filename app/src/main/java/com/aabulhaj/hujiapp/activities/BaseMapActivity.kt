package com.aabulhaj.hujiapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import com.aabulhaj.hujiapp.*
import com.aabulhaj.hujiapp.data.HUJIPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

open class BaseMapActivity : ToolbarActivity(), OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {
    protected var googleMap: GoogleMap? = null
        private set
    protected var mapView: MapView? = null
        private set
    protected var markerPlaces: HashMap<Marker, HUJIPlace> = HashMap()
    private val colors = intArrayOf(FLAT_PURPLE, FLAT_GREEN, FLAT_YELLOW, FLAT_ORANGE,
            Color.parseColor("#C78742"), FLAT_RED)

    // Hacky ..
    private var mSavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        mSavedInstanceState = savedInstanceState

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        mapView = findViewById(R.id.map)
        mapView?.onCreate(mSavedInstanceState)
        mapView?.getMapAsync(this)
        mSavedInstanceState = null
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.isBuildingsEnabled = true
        this.googleMap?.isIndoorEnabled = true

        verifyPermissionAndShowUserLocation()
        this.googleMap?.setOnInfoWindowClickListener(this)
    }

    private fun verifyPermissionAndShowUserLocation() {
        val hasPerm = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            try {
                googleMap?.isMyLocationEnabled = true
            } catch (e: SecurityException) {

            } catch (e: Exception) {

            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap?.isMyLocationEnabled = true
            } catch (e: SecurityException) {

            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val place = markerPlaces[marker]
        if (place?.streetViewCoordinate != null) {
            val intent = Intent(this, StreetViewActivity::class.java)
            intent.putExtra("latlng", place.streetViewCoordinate)
            startActivity(intent)
        }
    }

    protected fun setLocationByName(name: String): Marker? {
        val place = HUJIPlaceUtil.getPlaceFromName(name, this) ?: return null

        val m = googleMap?.addMarker(MarkerOptions().position(place.coordinate).title(place.name))
        moveCameraToPlace(place)
        if (m != null) {
            markerPlaces[m] = place
        }
        return m
    }

    protected fun moveCameraToPlace(hujiPlace: HUJIPlace) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(hujiPlace.coordinate, 17f))
    }
}