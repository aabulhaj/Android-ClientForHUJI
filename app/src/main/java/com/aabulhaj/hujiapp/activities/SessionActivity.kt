package com.aabulhaj.hujiapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aabulhaj.hujiapp.R
import kotlinx.android.synthetic.main.activity_session.*

class SessionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        setSupportActionBar(toolbar)
    }
}
