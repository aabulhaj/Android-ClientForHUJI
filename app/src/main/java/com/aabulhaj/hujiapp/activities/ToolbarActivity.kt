package com.aabulhaj.hujiapp.activities

import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.design.widget.AppBarLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*


open class ToolbarActivity : AppCompatActivity() {
    private var appBarLayout: AppBarLayout? = null

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)

        appBarLayout = toolbarFrame

        if (toolbar == null) {
            throw IllegalStateException("Used ToolbarActivity but forgot to include toolbar in XML")
        }

        setSupportActionBar(toolbar)
    }

    protected fun getAppBarLayout(): AppBarLayout? {
        return appBarLayout
    }

    @NonNull
    override fun getSupportActionBar(): ActionBar {
        return super.getSupportActionBar()!!
    }
}
