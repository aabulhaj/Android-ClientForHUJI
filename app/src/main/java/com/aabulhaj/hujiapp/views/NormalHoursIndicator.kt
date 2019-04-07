package com.aabulhaj.hujiapp.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import com.aabulhaj.hujiapp.R


class NormalHoursIndicator @JvmOverloads constructor(context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleRes: Int = 0)
    : View(context, attrs, defStyleRes) {
    private val paint = Paint()

    init {
        paint.color = ResourcesCompat.getColor(resources, R.color.google_green, null)
        paint.strokeWidth = 5f
    }

    fun setPaintColor(Color: Int) {
        paint.color = Color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)
    }
}
