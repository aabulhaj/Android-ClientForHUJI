package com.aabulhaj.hujiapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aabulhaj.hujiapp.R


open class RefreshListFragment : ListFragment(), SwipeRefreshLayout.OnRefreshListener,
        RefreshableFragment {
    private var refreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_list, container, false)
        refreshLayout = v.findViewById(R.id.refresh_layout)
        refreshLayout!!.setOnRefreshListener(this)
        val colors = resources.getIntArray(R.array.google_colors)
        refreshLayout!!.setColorSchemeColors(*colors)
        return v
    }

    override fun onRefresh() {

    }

    fun getRefreshLayout(): SwipeRefreshLayout {
        return refreshLayout!!
    }

    fun setRefreshing(refresh: Boolean) {
        refreshLayout?.isRefreshing = refresh
    }

    fun stopRefreshing() {
        setRefreshing(false)
    }

    override fun getFragment(): Fragment {
        return this
    }

    override fun refresh() {
        setRefreshing(true)
        onRefresh()
    }
}
