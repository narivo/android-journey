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

    private var dialDiameter = toPX(100)
    private var extraPadding = toPX(30)
    private var tickSize = toPX(10).toFloat()
    private var angleBetweenColors = 0f
    private var scale = 1f
    private var tickSizeScaled = tickSize * scale
    private var scaleToFit = false

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorDialView)
        try {
            val customColors = typedArray.getTextArray(R.styleable.ColorDialView_colors)
                ?.map {
                    Color.parseColor(it.toString())

                } as ArrayList<Int>?
            customColors?.let {
                colors = customColors

            }
            dialDiameter =  typedArray.getDimension(R.styleable.ColorDialView_dialDiameter, toPX(100).toFloat()).toInt()
            extraPadding =  typedArray.getDimension(R.styleable.ColorDialView_tickPadding, toPX(30).toFloat()).toInt()
            tickSize =  typedArray.getDimension(R.styleable.ColorDialView_tickRadius, toPX(10).toFloat())
            scaleToFit =  typedArray.getBoolean(R.styleable.ColorDialView_scaleToFit, false)
        } finally {
            typedArray.recycle()
        }
        dialDrawable = ContextCompat.getDrawable(context, R.drawable.ic_dial).also {
            it?.bounds = getCenteredBounds(dialDiameter)
            it?.setTint(Color.DKGRAY)
        }
        noColorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24).also {
            it?.bounds = getCenteredBounds(tickSize.toInt(), 2f)
        }
        colors.add(0, Color.TRANSPARENT)
        angleBetweenColors = 360f/ colors.size
        refreshValues(true)
    }

    private fun refreshValues(withScale: Boolean) {
        val localScale = if (withScale) scale else 1f

        // Compute padding values
        this.totalLeftPadding = paddingLeft + extraPadding * localScale
        this.totalRightPadding = paddingRight + extraPadding * localScale
        this.totalTopPadding = paddingTop + extraPadding * localScale
        this.totalBottomPadding = paddingBottom + extraPadding * localScale

        //Compute helper values
        this.horizontalSize = paddingLeft + paddingRight + (extraPadding  * localScale * 2) + dialDiameter * localScale
        this.verticalSize = paddingTop + paddingBottom + (extraPadding  * localScale * 2) + dialDiameter * localScale

        // Compute position values
        this.tickPositionVertical = paddingTop + extraPadding * localScale / 2f
        this.centerHorizontal = totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding)  / 2f
        this.centerVertical = totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
        tickSizeScaled = tickSize * scale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (scaleToFit) {
            refreshValues(false)
            val specWidth = MeasureSpec.getSize(widthMeasureSpec)
            val specHeight = MeasureSpec.getSize(heightMeasureSpec)
            val workingWidth = specWidth - paddingLeft - paddingRight
            val workingHeight = specHeight - paddingTop - paddingBottom
            scale = if (workingWidth < workingHeight) {
                (workingWidth) / (horizontalSize - paddingLeft - paddingRight)
            } else {
                (workingHeight) / (verticalSize - paddingTop - paddingBottom)
            }
            dialDrawable?.let {
                it.bounds = getCenteredBounds((dialDiameter * scale).toInt())

            }
            noColorDrawable?.let {
                it.bounds = getCenteredBounds((tickSize * scale).toInt())

            }

            val width = resolveSizeAndState((horizontalSize * scale).toInt(), widthMeasureSpec, 0)
            val height = resolveSizeAndState((verticalSize * scale).toInt(), heightMeasureSpec, 0)
            refreshValues(true)
            setMeasuredDimension(width, height)
        } else {
            val width = resolveSizeAndState(horizontalSize.toInt(), widthMeasureSpec, 0)
            val height = resolveSizeAndState(verticalSize.toInt(), heightMeasureSpec, 0)
            setMeasuredDimension(width, height)
        }

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
                canvas.drawCircle(centerHorizontal, tickPositionVertical, tickSizeScaled, paint)

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

    private fun toPX(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics).toInt()
    }
}