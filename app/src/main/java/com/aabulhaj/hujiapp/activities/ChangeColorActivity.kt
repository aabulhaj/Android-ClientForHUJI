package com.aabulhaj.hujiapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.aabulhaj.hujiapp.CourseToColorMapper
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.Course
import kotlinx.android.synthetic.main.activity_change_color.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar


class ChangeColorActivity : ToolbarActivity() {
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_color)

        course = intent.getSerializableExtra("course") as Course
        val color = CourseToColorMapper.getColor(course)

        colorView.setBackgroundColor(color)
        redSeedBar.progress = Color.red(color)
        greenSeekBar.progress = Color.green(color)
        blueSeekBar.progress = Color.blue(color)

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = getString(R.string.change_color)

        redSeedBar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                colorView.setBackgroundColor(
                        Color.rgb(
                                redSeedBar.progress,
                                greenSeekBar.progress,
                                blueSeekBar.progress)
                )
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {}
        })

        greenSeekBar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                colorView.setBackgroundColor(
                        Color.rgb(
                                redSeedBar.progress,
                                greenSeekBar.progress,
                                blueSeekBar.progress)
                )
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {}
        })

        blueSeekBar.setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, progress: Int, fromUser: Boolean) {
                colorView.setBackgroundColor(
                        Color.rgb(
                                redSeedBar.progress,
                                greenSeekBar.progress,
                                blueSeekBar.progress)
                )
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}
            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.change_color_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item?.itemId == R.id.save) {
            CourseToColorMapper.setColor(
                    course,
                    Color.rgb(redSeedBar.progress, greenSeekBar.progress, blueSeekBar.progress),
                    this
            )
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
