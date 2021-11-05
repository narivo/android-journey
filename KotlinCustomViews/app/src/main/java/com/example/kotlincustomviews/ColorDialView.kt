package com.example.kotlincustomviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat

/**
 * TODO: document your custom view class.
 */
class ColorDialView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0,
                                              defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes) {

    private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.YELLOW,
        Color.BLUE, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.MAGENTA,
        Color.BLACK)

    private var noColorDrawable: Drawable? = null
    private var dialDrawable: Drawable? = null
    private val paint = Paint().also{
        it.color = Color.BLUE
        it.isAntiAlias = true

    }

    private var dialDiameter = toDP(100)
    private var extraPadding = toDP(30)
    private var tickSize = toDP(10).toFloat()
    private var angleBetweenColors = 0f

    // Pre-computed padding values
    private var totalLeftPadding = 0f
    private var totalTopPadding = 0f
    private var totalRightPadding = 0f
    private var totalBottomPadding = 0f

    // Pre-computed helper values
    private var horizontalSize = 0f
    private var verticalSize = 0f

    // Pre-computed position values
    private var tickPositionVertical = 0f
    private var centerHorizontal = 0f
    private var centerVertical = 0f

    init {

        dialDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dial).also {
            it?.bounds = getCenteredBounds(dialDiameter)
            it?.setTint(Color.DKGRAY)
        }
        noColorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24).also {
            it?.bounds = getCenteredBounds(tickSize.toInt(), 2f)
        }
        colors.add(0, Color.TRANSPARENT)
        angleBetweenColors = 360f/ colors.size
        refreshValues()
    }

    private fun refreshValues() {
        // Compute padding values
        this.totalLeftPadding = (paddingLeft + extraPadding).toFloat()
        this.totalRightPadding = (paddingRight + extraPadding).toFloat()
        this.totalTopPadding = (paddingTop + extraPadding).toFloat()
        this.totalBottomPadding = (paddingBottom + extraPadding).toFloat()

        //Compute helper values
        this.horizontalSize = paddingLeft + paddingRight + (extraPadding * 2) + dialDiameter.toFloat()
        this.verticalSize = paddingTop + paddingBottom + (extraPadding * 2) + dialDiameter.toFloat()

        // Compute position values
        this.tickPositionVertical = paddingTop + extraPadding / 2f
        this.centerHorizontal = totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding)  / 2f
        this.centerVertical = totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        val saveCount = canvas.save()
        colors.forEachIndexed { index, color ->
            if (index == 0) {
                canvas.translate(centerHorizontal, tickPositionVertical)
                noColorDrawable?.draw(canvas)
                canvas.translate(-centerHorizontal, -tickPositionVertical)
            } else {
                paint.color = colors[index]
                canvas.drawCircle(centerHorizontal, tickPositionVertical, tickSize, paint)

            }
            canvas.rotate(angleBetweenColors, centerHorizontal, centerVertical)
        }
        canvas.restoreToCount(saveCount)

        canvas.translate(centerHorizontal, centerVertical)
        dialDrawable?.draw(canvas)
    }

    private fun getCenteredBounds(size: Int, scalar: Float = 1f): Rect {
        val half = ((if (size > 0) size / 2 else 1) * scalar).toInt()
        return Rect(-half, -half, half, half)
    }

    private fun toDP(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics).toInt()
    }
}