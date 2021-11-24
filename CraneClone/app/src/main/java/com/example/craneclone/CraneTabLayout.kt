package com.example.craneclone

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.tabs.TabLayout
import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap
import android.graphics.drawable.RippleDrawable
import androidx.core.view.ViewCompat
import com.google.android.material.ripple.RippleUtils


class CraneTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f

    var oldLeft = 0f
    var oldRight = 0f

    val indicatorPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 5f
    }

    private var firstLaunch = true

    val craneTabSelectedListener = object: OnTabSelectedListener {
        override fun onTabSelected(tab: Tab?) {
            Log.d("CraneTabLayout", "Tab position $selectedTabPosition")

            if (firstLaunch == false) {
                updateTabOutline()

                val animatorSet = AnimatorSet()

                Log.d("CraneTabLayout", "new right $right")

                var leftAnimator = ValueAnimator.ofFloat(oldLeft, left).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 250L
                    addUpdateListener {
                        left = it.animatedValue as Float
                        invalidate()
                    }
                }
                var rightAnimator = ValueAnimator.ofFloat(oldRight, right).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 250L
                    addUpdateListener {
                        right = it.animatedValue as Float
                    }
                }

                val animations = listOf(leftAnimator, rightAnimator)
                animatorSet.playTogether(animations).also {
                    animatorSet.start()
                }
            }
        }

        override fun onTabUnselected(tab: Tab?) {

        }

        override fun onTabReselected(tab: Tab?) {

        }

    }

    private fun updateTabOutline() {

        val tabWidth = width / tabCount
        val fourthTabWidth = tabWidth * 1/4

        oldLeft = left
        oldRight = right

        left = getTabAt(selectedTabPosition)?.view?.left?.toFloat() ?: 0f
        top = getTabAt(selectedTabPosition)?.view?.top?.toFloat() ?: 0f
        right = getTabAt(selectedTabPosition)?.view?.right?.toFloat() ?: 0f
        bottom = getTabAt(selectedTabPosition)?.view?.bottom?.toFloat() ?: 0f

        Log.d("CraneTabLayout", "#updateTabOutline new right $right")
        
        left = left * 3/4 + right * 1/4
        top = top * 3/4 + bottom * 1/4
        right -= fourthTabWidth
        bottom = bottom * 3/4

    }

    init {
        addOnTabSelectedListener(craneTabSelectedListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (firstLaunch) {
            updateTabOutline()
            firstLaunch = false
        }
        //canvas.drawRect(tabRect, indicatorPaint)
        canvas.drawRoundedRect(left, top, right, bottom, toPX(10).toFloat(), toPX(10).toFloat(), indicatorPaint)
    }

    private fun toPX(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics).toInt()
    }
}

fun Canvas.drawRoundedRect(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float, paint: Paint) {
    val path = roundedRect(left, top, right, bottom, rx, ry)
    this.drawPath(path, paint)
}

private fun Canvas.roundedRect(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float): Path {
    var rx = rx
    var ry = ry

    val path = Path()
    if (rx < 0) rx = 0f
    if (ry < 0) ry = 0f

    val width = right - left
    val height = bottom - top

    if (rx > width / 2) rx = width / 2
    if (ry > height / 2) ry = height / 2

    val widthMinusCorners = width - 2 * rx
    val heightMinusCorners = height - 2 * ry

    path.moveTo(right, top + ry)

    path.arcTo(right - 2 * rx, top, right, top + 2 * ry, 0f, -90f, false) //top-right-corner
    path.rLineTo(-widthMinusCorners, 0f)

    path.arcTo(left, top, left + 2 * rx, top + 2 * ry, 270f, -90f, false) //top-left corner.
    path.rLineTo(0f, heightMinusCorners)

    path.arcTo(left, bottom - 2 * ry, left + 2 * rx, bottom, 180f, -90f, false) //bottom-left corner
    path.rLineTo(widthMinusCorners, 0f)

    path.arcTo(right - 2 * rx, bottom - 2 * ry, right, bottom, 90f, -90f, false) //bottom-right corner
    path.rLineTo(0f, -heightMinusCorners)

    path.close() //Given close, last lineto can be removed.

    return path
}