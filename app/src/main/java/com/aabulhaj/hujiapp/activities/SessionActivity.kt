package com.aabulhaj.hujiapp.activities

import Session
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.fragments.*
import com.aabulhaj.hujiapp.util.PreferencesUtil
import kotlinx.android.synthetic.main.activity_session.*


class SessionActivity : ToolbarActivity() {
    private var coursesFragment: Fragment? = null
    private var tableFragment: Fragment? = null
    private var aboutMeFragment: Fragment? = null
    private var mapFragment: Fragment? = null
    private var moreFragment: Fragment? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Session.INTENT_APP_LOGGED_OUT == intent.action) {
                val i = Intent(context, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        supportActionBar.setDisplayShowTitleEnabled(false)

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Session.INTENT_APP_LOGGED_OUT))

        val fragmentManager = this.supportFragmentManager

        // Show last chosen fragment.
        val lastFragId = PreferencesUtil.getIntOr(
                Session.getCacheKey("last_tab"),
                R.id.action_about_me)

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, getFragment(lastFragId))
                .commit()
        bottomNavView.selectedItemId = lastFragId

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            val fragment = getFragment(item.itemId)

            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()

            PreferencesUtil.putInt(Session.getCacheKey("last_tab"), item.itemId)

            return@setOnNavigationItemSelectedListener true
        }

        bottomNavView.setOnNavigationItemReselectedListener { item ->

        }
    }

    private fun getFragment(itemId: Int): Fragment? {
        when (itemId) {
            R.id.action_courses -> {
                if (coursesFragment == null) coursesFragment = CoursesFragment()
                return coursesFragment
            }
            R.id.action_timetable -> {
                if (tableFragment == null) tableFragment = TableFragment()
                return tableFragment
            }
            R.id.action_about_me -> {
                if (aboutMeFragment == null) aboutMeFragment = AboutMeFragment()
                return aboutMeFragment
            }
            R.id.action_map -> {
                if (mapFragment == null) mapFragment = MapFragment()
                return mapFragment
            }
            R.id.action_more -> {
                if (moreFragment == null) moreFragment = MoreFragment()
                return moreFragment
            }
        }
        return null
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }
}
