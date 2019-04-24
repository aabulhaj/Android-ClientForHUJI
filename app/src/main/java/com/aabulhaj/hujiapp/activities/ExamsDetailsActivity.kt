package com.aabulhaj.hujiapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.Exam
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_exams_details.*

class ExamsDetailsActivity : BaseMapActivity() {
    private lateinit var exam: Exam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exams_details)

        supportActionBar.setDisplayHomeAsUpEnabled(true)

        exam = intent.getSerializableExtra("exam") as Exam
        supportActionBar.title = exam.course?.name

        if (exam.room.isNullOrEmpty() && exam.roomsSpecial.isNullOrEmpty()) {
            blurredMapView.visibility = View.VISIBLE
            locationNotAvTextView.visibility = View.VISIBLE

            blurredMapView.isClickable = false
            roomsSwitch.isClickable = false
            viewRoomsButton.isClickable = false
            return
        }

        blurredMapView.visibility = View.GONE
        locationNotAvTextView.visibility = View.GONE

        if (exam.room.isNullOrEmpty()) {
            room.visibility = View.GONE
            roomTitle.visibility = View.GONE
        } else {
            roomTitle.text = exam.room
        }

        if (exam.roomsSpecial.isNullOrEmpty()) {
            specialRoom.visibility = View.GONE
            specialRoomTitle.visibility = View.GONE
            roomsSwitch.visibility = View.GONE
        } else {
            specialRoomTitle.text = exam.room
        }

        viewRoomsButton.setOnClickListener {
            val intent = Intent(this, ExtraRoomsActivity::class.java)
            intent.putExtra("exam", exam)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        exam = intent.getSerializableExtra("exam") as Exam

        var normalMarker: Marker? = null
        var specialMarker: Marker? = null
        if (!exam.room.isNullOrEmpty()) {
            normalMarker = setLocationByName(exam.room!!)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(normalMarker?.position, 17f))
        }
        if (!exam.roomsSpecial.isNullOrEmpty()) {
            specialMarker = setLocationByName(exam.roomsSpecial!!)
        }

        roomsSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(specialMarker?.position, 17f))
                specialMarker?.showInfoWindow()
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(normalMarker?.position, 17f))
                normalMarker?.showInfoWindow()
            }
        }
    }
}
