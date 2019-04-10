package com.aabulhaj.hujiapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aabulhaj.hujiapp.R
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_street_view.*


class StreetViewActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        panoView.onCreate(savedInstanceState)

        panoView.getStreetViewPanoramaAsync(this)
    }

    override fun onPause() {
        panoView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        panoView?.onResume()
    }

    override fun onDestroy() {
        panoView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        panoView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        panoView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStreetViewPanoramaReady(p0: StreetViewPanorama?) {
        val latLon = intent.getParcelableExtra<LatLng>("latlng")
        p0?.setPosition(latLon)
    }
}