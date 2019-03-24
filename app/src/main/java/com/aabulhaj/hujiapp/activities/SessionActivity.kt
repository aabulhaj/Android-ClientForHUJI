package com.aabulhaj.hujiapp.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.fragments.*
import kotlinx.android.synthetic.main.activity_session.*


class SessionActivity : AppCompatActivity() {
    private var coursesFragment: Fragment? = null
    private var tableFragment: Fragment? = null
    private var aboutMeFragment: Fragment? = null
    private var mapFragment: Fragment? = null
    private var moreFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        val fragmentManager = this.supportFragmentManager

        // Show AboutMe fragment on startup.
        val aboutMeId = R.id.action_about_me
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, getFragment(aboutMeId))
                .commit()
        bottomNavView.selectedItemId = aboutMeId

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            val fragment = getFragment(item.itemId)

            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()

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
}
