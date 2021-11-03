package com.example.kotlincustomviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.core.content.ContextCompat

class ColorSlider @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = R.attr.seekBarStyle,
                                            defStyleRes: Int = 0)
    : androidx.appcompat.widget.AppCompatSeekBar(context, attrs, defStyleAttr) {
    private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.YELLOW, Color.BLUE)

    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            var idx = colors.indexOf(value)
            if(idx == -1) {
                progress = 0
            } else {
                progress = idx
            }
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSlider)
        try {
            colors = typedArray.getTextArray(R.styleable.ColorSlider_colors)
                .map {
                    Color.parseColor(it.toString())

                } as ArrayList<Int>

        } finally {
            typedArray.recycle()
        }

        colors.add(0, Color.TRANSPARENT)
        max = colors.size - 1
        progressBackgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        progressTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        splitTrack = false
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 50)
        thumb = ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_drop_down_24)

        setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                listeners.forEach{
                    it(colors[p1])

                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }

    private fun drawTickMarks(canvas: Canvas?) {
        canvas?.let {
            val count = colors.size
            val saveCount = canvas.save()
            canvas.translate(paddingStart.toFloat(), (height/2).toFloat() + 50f)
            if(count > 1) {
                for(i in 0 until count) {
                    val w = 48f
                    val h = 48f
                    val halfw = if(w >= 0) w/2 else 1f
                    val halfh = if (h >= 0) h/2 else 1f

                    val spacing = (width - paddingStart - paddingEnd) / (count - 1).toFloat()

                    if(i == 0) {
                        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24)
                        val w2 = drawable?.intrinsicWidth ?: 0
                        val h2 = drawable?.intrinsicHeight ?: 0
                        val halfw2 = if (w2 >= 0) w2/2 else 1
                        val halfh2 = if (h2 >= 0) h2/2 else 1
                        drawable?.setBounds(-halfw2, -halfh2, halfw2, halfh2)
                        drawable?.draw(canvas)
                    } else {

                        val paint = Paint()
                        paint.color = colors[i]

                        canvas.drawRect(-halfw, -halfh, halfw, halfh, paint)
                    }
                    canvas.translate(spacing, 0f)
                }
                canvas.restoreToCount(saveCount)
            }

        }
    }

    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()

    fun addListener(function: (Int) -> Unit) {
        listeners.add(function)
    }
}