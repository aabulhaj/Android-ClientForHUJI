package com.aabulhaj.hujiapp.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.MenuItem
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.PinnedSectionsAdapter
import com.aabulhaj.hujiapp.util.PreferencesUtil
import kotlinx.android.synthetic.main.activity_campus_shuttle.*


class CampusShuttleTimesActivity : ToolbarActivity() {
    private var showEdmondShuttles: Boolean = false
    private lateinit var shuttleAdapter: PinnedSectionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus_shuttle)

        shuttleAdapter = PinnedSectionsAdapter(this)

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setTitle(R.string.campus_shuttle_times)
        supportActionBar.elevation = 0f

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.emond_safra)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.mount_scopus)))

        showEdmondShuttles = PreferencesUtil.getBoolean("shuttle_campus")

        tabLayout.getTabAt(if (showEdmondShuttles) 0 else 1)?.select()

        addDataToList()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showEdmondShuttles = tab.position == 0
                PreferencesUtil.putBoolean("shuttle_campus", showEdmondShuttles)
                addDataToList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun addDataToList() {
        shuttleAdapter = PinnedSectionsAdapter(this)
        if (showEdmondShuttles) fillEdmondData() else fillMountData()
        shuttleListView.adapter = shuttleAdapter
    }

    private fun fillEdmondData() {
        shuttleAdapter.addSectionHeaderItem(getString(R.string.from_high_Tech))
        shuttleAdapter.addItem("7:45 AM")
        shuttleAdapter.addItem("9:50 AM")
        shuttleAdapter.addItem("11:50 AM")
        shuttleAdapter.addItem("1:50 PM")
        shuttleAdapter.addItem("3:50 PM")
        shuttleAdapter.addItem("5:50 PM")
        shuttleAdapter.addSectionHeaderItem(getString(R.string.from_main_gate))
        shuttleAdapter.addItem("7:50 AM")
        shuttleAdapter.addItem("9:55 AM")
        shuttleAdapter.addItem("11:55 AM")
        shuttleAdapter.addItem("1:55 PM")
        shuttleAdapter.addItem("3:55 PM")
        shuttleAdapter.addItem("5:55 PM")
        shuttleAdapter.notifyDataSetChanged()
    }

    private fun fillMountData() {
        shuttleAdapter.addSectionHeaderItem(getString(R.string.from_bus_tunnel))
        shuttleAdapter.addItem("7:05 AM")
        shuttleAdapter.addItem("9:15 AM")
        shuttleAdapter.addItem("11:15 AM")
        shuttleAdapter.addItem("1:15 PM")
        shuttleAdapter.addItem("3:15 PM")
        shuttleAdapter.addItem("5:15 PM")
        shuttleAdapter.addItem("6:30 PM")
        shuttleAdapter.addSectionHeaderItem(getString(R.string.from_student_village))
        shuttleAdapter.addItem("7:10 AM")
        shuttleAdapter.addItem("9:20 AM")
        shuttleAdapter.addItem("11:20 AM")
        shuttleAdapter.addItem("1:20 PM")
        shuttleAdapter.addItem("3:20 PM")
        shuttleAdapter.addItem("5:20 PM")
        shuttleAdapter.addItem("6:35 PM")
        shuttleAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}
