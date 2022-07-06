package com.example.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class NameEditText : TextInputEditText, View.OnTouchListener {
    private lateinit var clearButton: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setBackgroundResource(R.drawable.border_corner)
        textSize = 15f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(p0: Editable) {
                if (p0.toString().isNotEmpty()) {
                    showClearButton()
                } else {
                    hideClearButton()
                }

                if (p0.toString().isEmpty()) {
                    showError()
                }
            }

        })
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButton)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun showError() {
        error = context.getString(R.string.must_fill)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(p0: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) isClearButtonClicked = true
            }

            if (isClearButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        showClearButton()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        if (text != null) text?.clear()
                        hideClearButton()
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}