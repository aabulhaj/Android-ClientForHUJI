package com.aabulhaj.hujiapp.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import com.aabulhaj.hujiapp.R
import java.io.IOException


class RoundedImageView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleRes: Int = 0)
    : ImageView(context, attrs, defStyleRes) {
    private var paint: Paint
    private var bitmapPaint: Paint
    private var rectF: RectF? = null
    private var shader: Shader? = null

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f, resources.displayMetrics)
        paint.color = resources.getColor(R.color.colorAccent)
        paint.style = Paint.Style.STROKE

        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun setImageURI(uri: Uri) {
        super.setImageURI(uri)

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val strokeWidth = paint.strokeWidth
        rectF = RectF(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth)
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        shader = BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable == null) {
            return
        }
        val b = (drawable as BitmapDrawable).bitmap
        shader = BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        if (rectF == null)
            return

        val strokeWidth = paint.strokeWidth

        canvas.drawArc(rectF, 0f, 360f, false, paint)

        if (drawable == null || shader == null)
            return

        bitmapPaint.shader = shader

        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val radius = Math.max(halfWidth, halfHeight) - strokeWidth
        canvas.drawCircle(halfWidth, halfHeight, radius, bitmapPaint)
    }
}
