package com.example.craneclone

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.tabs.TabLayout

class CraneTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    var newLeft = 0f
    var newTop = 0f
    var newRight = 0f
    var newBottom = 0f

    var tabRect: RectF = RectF()

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

                Log.d("CraneTabLayout", "new right $newRight")

                var leftAnimator = ValueAnimator.ofFloat(oldLeft, newLeft).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 250L
                    addUpdateListener {
                        tabRect.left = it.animatedValue as Float
                        invalidate()
                    }
                }
                var rightAnimator = ValueAnimator.ofFloat(oldRight, newRight).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 250L
                    addUpdateListener {
                        tabRect.right = it.animatedValue as Float
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

        oldLeft = tabRect.left
        oldRight = tabRect.right

        newLeft = getTabAt(selectedTabPosition)?.view?.left?.toFloat() ?: 0f
        newTop = getTabAt(selectedTabPosition)?.view?.top?.toFloat() ?: 0f
        newRight = getTabAt(selectedTabPosition)?.view?.right?.toFloat() ?: 0f
        newBottom = getTabAt(selectedTabPosition)?.view?.bottom?.toFloat() ?: 0f

        Log.d("CraneTabLayout", "#updateTabOutline new right $newRight")
        
        newLeft = newLeft * 3/4 + newRight * 1/4
        newTop = newTop * 3/4 + newBottom * 1/4
        newRight -= fourthTabWidth
        newBottom = newBottom * 3/4

        tabRect = RectF(newLeft, newTop, newRight, newBottom)
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
        canvas.drawRect(tabRect, indicatorPaint)
    }

    private fun toPX(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics).toInt()
    }
}