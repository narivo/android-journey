package com.example.kotlincustomviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.color_selector.view.*

class ColorSelector @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null,
                defStyle: Int = 0, defRes: Int = 0)
    : LinearLayout(context, attributeSet, defStyle, defRes) {

    private var listOfColors = listOf(Color.BLUE, Color.RED, Color.GREEN)
    private var selectedColorIndex = 0

    private var listeners: ArrayList<((Int) -> Unit)?> = arrayListOf()

    var currentColor: Int = Color.TRANSPARENT
        set(value) {
            enableColorCheckbox.isChecked = true
            selectedColor.setBackgroundColor(value)
        }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColorSelector)
        listOfColors = typedArray.getTextArray(R.styleable.ColorSelector_colors)
            .map {
                Color.parseColor(it.toString())

            }
        typedArray.recycle()

        orientation = HORIZONTAL
        gravity = CENTER

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.color_selector, this)

        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])

        colorSelectorArrowLeft.setOnClickListener {
            selectPreviousColor()

        }

        colorSelectorArrowRight.setOnClickListener {
            selectNextColor()

        }

        enableColorCheckbox.setOnCheckedChangeListener { _, _ -> broadcastColor() }
    }

    fun addListener(listener: ((Int) -> Unit)?) {
        this.listeners.add(listener)
    }

    private fun broadcastColor() {
        val color = if(enableColorCheckbox.isChecked) listOfColors[selectedColorIndex] else Color.TRANSPARENT
        listeners.forEach { elem ->
            elem?.invoke(color)

        }
    }

    private fun selectNextColor() {
        if(selectedColorIndex == listOfColors.lastIndex) {
            selectedColorIndex = 0
        } else {
            selectedColorIndex++
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()
    }

    private fun selectPreviousColor() {
        if(selectedColorIndex == 0) {
            selectedColorIndex = listOfColors.lastIndex
        } else {
            selectedColorIndex--
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()

    }

}