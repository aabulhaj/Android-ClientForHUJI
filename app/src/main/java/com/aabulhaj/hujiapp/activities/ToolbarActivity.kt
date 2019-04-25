package com.aabulhaj.hujiapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.design.widget.AppBarLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.View
import com.aabulhaj.hujiapp.R
import kotlinx.android.synthetic.main.toolbar.*


open class ToolbarActivity : AppCompatActivity() {
    private var appBarLayout: AppBarLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme_NoActionBarTranslucent)
        }

        super.onCreate(savedInstanceState)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)

        appBarLayout = toolbarFrame

        if (toolbar == null) {
            throw IllegalStateException("Used ToolbarActivity but forgot to include toolbar in XML")
        }

        toolbar.setLogo(R.drawable.logo)
        try {
            val f = Toolbar::class.java.getDeclaredField("mLogoView")
            f.isAccessible = true
            (f.get(toolbar) as View).setOnClickListener({ _: View ->
                val url = "http://huji.ac.il"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            })
        } catch (e: Exception) {
            e.printStackTrace()
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
