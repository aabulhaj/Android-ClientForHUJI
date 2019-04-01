package com.aabulhaj.hujiapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.R
import com.booking.rtlviewpager.RtlViewPager
import kotlinx.android.synthetic.main.fragment_courses_fragments_holder.view.*


class CoursesFragmentsHolder : Fragment(), RefreshableFragment {
    private var coursesFragment: CoursesFragment? = null
    private var examsFragment: ExamsFragment? = null
    private var noteBooksFragment: NoteBooksFragment? = null
    private var lastViewPagerPagePositionSelected: Int = 0

    private var rtlViewPager: RtlViewPager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_courses_fragments_holder, container, false)

        rtlViewPager = view.rtlViewPager

        rtlViewPager?.adapter = MyAdapter(childFragmentManager)
        view.tabs.setupWithViewPager(rtlViewPager)

        rtlViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                refreshIfNeeded(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        return view
    }

    private fun refreshIfNeeded(position: Int) {
        if (lastViewPagerPagePositionSelected != position) {
            lastViewPagerPagePositionSelected = position
            return
        }
        refreshFragment(position)
    }

    private fun refreshFragment(position: Int) {
        when (position) {
            0 -> coursesFragment?.refresh()
            1 -> examsFragment?.refresh()
            2 -> noteBooksFragment?.refresh()
        }
    }

    inner class MyAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val tabTitles = arrayOf(getString(R.string.grades),
                getString(R.string.exams), getString(R.string.note_book))

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> {
                    if (coursesFragment == null) {
                        coursesFragment = CoursesFragment()
                    }
                    return coursesFragment
                }
                1 -> {
                    if (examsFragment == null) {
                        examsFragment = ExamsFragment()
                    }
                    return examsFragment
                }
                2 -> {
                    if (noteBooksFragment == null) {
                        noteBooksFragment = NoteBooksFragment()
                    }
                    return noteBooksFragment
                }
            }
            return null
        }

        override fun getPageTitle(position: Int): String? {
            return tabTitles[position]
        }
    }

    override fun refresh() {
        if (rtlViewPager != null) {
            refreshFragment(rtlViewPager!!.currentItem)
        }
    }

    override fun getFragment(): Fragment {
        return this
    }
}
