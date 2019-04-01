package com.aabulhaj.hujiapp.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.aabulhaj.hujiapp.R


class GradeProgressBar @JvmOverloads constructor(context: Context,
                                                 attributeSet: AttributeSet? = null,
                                                 defStyle: Int = 0) :
        View(context, attributeSet, defStyle) {
    companion object {
        private val FLAT_RED = Color.rgb(231, 76, 60)
        private val FLAT_GREEN = Color.rgb(46, 204, 113)
    }

    private val paint: Paint
    private var rectF: RectF? = null

    private val argbEvaluator = ArgbEvaluator()

    private var percentage = 0

    private var padding = 0

    private var hujiColor: Int = 0

    init {
        hujiColor = resources.getColor(R.color.colorAccent, context.theme)

        hujiColor = Color.argb(40, Color.red(hujiColor), Color.green(hujiColor), Color.blue(hujiColor))

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = FLAT_GREEN
        paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
        paint.style = Paint.Style.STROKE

        padding = TypedValue.applyDimension(2, TypedValue.COMPLEX_UNIT_DIP.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val newPadding = paint.strokeWidth + padding
        rectF = RectF(newPadding, newPadding, w - newPadding, h - newPadding)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    fun setPercent(percent: Int, animated: Boolean) {
        var percent = percent
        if (percent > 100) {
            percent = 100
        } else if (percent < 0) {
            percent = 0
        }

        if (animated) {
            val animator = ValueAnimator.ofInt(percentage, percent)
            animator.addUpdateListener { animation ->
                percentage = animation.animatedValue as Int

                postInvalidate()
            }
            animator.duration = 500L
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        } else {
            percentage = percent
            postInvalidate()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rectF == null) {
            return
        }

        val startAngle = -90f
        var sweepAngle = 360f

        paint.color = hujiColor
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)

        if (percentage <= 0)
            return

        val percentFraction = percentage.toFloat() / 100f
        sweepAngle = percentage * 360f / 100f
        paint.color = argbEvaluator.evaluate(percentFraction, FLAT_RED, FLAT_GREEN) as Int

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
    }
}