package com.aabulhaj.hujiapp.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


class MarkCellHintView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyle: Int = 0) :
        View(context, attrs, defStyle) {
    companion object {
        private const val CORNER_RADIUS = 8f
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint
    private var showStatisticsHint: Boolean = false
    private var showExtraMarkHint: Boolean = false
    private var bounds: RectF? = null

    init {
        paint.color = Color.argb(77, 38, 165, 148)
        paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f,
                resources.displayMetrics)
        paint.style = Paint.Style.STROKE

        textPaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f,
                resources.displayMetrics)
        textPaint.color = paint.color
    }

    fun setShowStatisticsHint(show: Boolean) {
        showStatisticsHint = show
        postInvalidate()
    }

    fun setShowExtraMarksHint(show: Boolean) {
        showExtraMarkHint = show
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val paintPadding = paint.strokeWidth
        val squareSize = width - paintPadding
        val top = height / 2 - squareSize / 2
        bounds = RectF(paintPadding, top, squareSize, top + squareSize)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (!showStatisticsHint && !showExtraMarkHint || bounds == null) {
            return
        }

        val paintPadding = paint.strokeWidth
        val squareSize = width - paintPadding

        val boundsVal = bounds!!

        if (showStatisticsHint && showExtraMarkHint) {
            val origTop = boundsVal.top
            val origBottom = boundsVal.bottom

            boundsVal.top = paintPadding
            boundsVal.bottom = boundsVal.top + squareSize

            canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, paint)
            drawTextOnRectF(canvas, boundsVal, "s")

            boundsVal.bottom = height - paintPadding
            boundsVal.top = boundsVal.bottom - squareSize

            canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, paint)
            drawTextOnRectF(canvas, boundsVal, "+")

            boundsVal.top = origTop
            boundsVal.bottom = origBottom
        } else if (showStatisticsHint) {
            canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, paint)
            drawTextOnRectF(canvas, boundsVal, "s")
        } else if (showExtraMarkHint) {
            canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, paint)
            drawTextOnRectF(canvas, boundsVal, "+")
        }
    }

    private fun drawTextOnRectF(canvas: Canvas, rectF: RectF, text: String) {
        val bounds = RectF(rectF)
        bounds.right = textPaint.measureText(text)
        bounds.bottom = textPaint.descent() - textPaint.ascent()

        bounds.left += (rectF.width() - bounds.right) / 2.0f
        bounds.top += (rectF.height() - bounds.bottom) / 2.0f

        canvas.drawText(text, bounds.left, bounds.top - textPaint.ascent(), textPaint)
    }
}