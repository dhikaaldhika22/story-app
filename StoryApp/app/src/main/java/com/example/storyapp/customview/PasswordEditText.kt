package com.example.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText, View.OnTouchListener {

    private lateinit var hideIcon: Drawable

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        showHideButton()
        setBackgroundResource(R.drawable.border_corner)
        textSize = 15f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


    private fun init()  {
        hideIcon = ContextCompat.getDrawable(context, R.drawable.btn_hide_off) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(p0: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(p0: Editable) {
                if (p0.toString().length < 6) {
                    showError()
                }
            }

        })
    }

    private fun showError() {
        error = context.getString(R.string.invalid_password)
    }

    private fun showHideButton() {
        setButtonDrawables(endOfTheText = hideIcon)
    }

    private fun hideButton() {
        setButtonDrawables()
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

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val hideButtonStart: Float
            val hideButtonEnd: Float
            var isHideButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                hideButtonEnd = (hideIcon.intrinsicWidth + paddingStart).toFloat()
                if (event.x < hideButtonEnd) isHideButtonClicked = true
            } else {
                hideButtonStart = (width - paddingEnd - hideIcon.intrinsicWidth).toFloat()
                if (event.x > hideButtonStart) isHideButtonClicked = true
            }

            if (isHideButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance()
                            hideIcon = ContextCompat.getDrawable(context, R.drawable.btn_hide_off) as Drawable
                            showHideButton()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance()
                            hideIcon = ContextCompat.getDrawable(context, R.drawable.btn_hide_on) as Drawable
                            showHideButton()
                        }
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}