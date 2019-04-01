package com.aabulhaj.hujiapp.fragments

import android.support.v4.app.Fragment

interface RefreshableFragment {
    fun getFragment(): Fragment
    fun refresh()
}
