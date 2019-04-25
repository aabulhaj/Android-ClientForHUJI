package com.aabulhaj.hujiapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.*
import com.aabulhaj.hujiapp.activities.StreetViewActivity
import com.aabulhaj.hujiapp.data.HUJIPlace
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


open class BaseMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    protected var googleMap: GoogleMap? = null
        private set
    protected var mapView: MapView? = null
        private set
    private var v: View? = null
    protected var markerPlaces: HashMap<Marker, HUJIPlace> = HashMap()
    private val colors = intArrayOf(FLAT_PURPLE, FLAT_GREEN, FLAT_YELLOW, FLAT_ORANGE,
            Color.parseColor("#C78742"), FLAT_RED)

    protected fun setView(v: View) {
        this.v = v
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mapView = v?.findViewById(R.id.map) as MapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        return v
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

        var style: MapStyleOptions? = null
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.mapstyle_night)
        }
        this.googleMap?.setMapStyle(style)
    }

    private fun verifyPermissionAndShowUserLocation() {
        val hasPerm = context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
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
            val intent = Intent(activity, StreetViewActivity::class.java)
            intent.putExtra("latlng", place.streetViewCoordinate)
            activity?.startActivity(intent)
        }
    }

    protected fun setLocationByName(name: String): Marker? {
        val place = HUJIPlaceUtil.getPlaceFromName(name, context) ?: return null

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

    protected fun addPlaceToMap(place: HUJIPlace) {
        val m = googleMap?.addMarker(MarkerOptions().position(place.coordinate).title(place.name))
        if (m != null) {
            markerPlaces[m] = place
        }
    }

    protected fun addPlaceToMapWithColor(place: HUJIPlace, moveCamera: Boolean, animate: Boolean) {
        val hsv = FloatArray(3)
        Color.colorToHSV(colors[place.type.getValue()], hsv)
        val m = googleMap?.addMarker(MarkerOptions().position(place.coordinate).title(place.name)
                .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])))
        if (moveCamera) {
            if (animate) {
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(m?.position, 17f))
            } else {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(m?.position, 17f))
            }
            m?.showInfoWindow()
        }
        if (m != null) {
            markerPlaces[m] = place
        }
    }
}
