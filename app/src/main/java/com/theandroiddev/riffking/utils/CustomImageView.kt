package com.theandroiddev.riffking.utils

import android.content.Context
import android.util.AttributeSet

/**
 * Created by jakub on 04.10.17.
 */

class CustomImageView : android.support.v7.widget.AppCompatImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth

        //force a 16:9 aspect ratio
        val height = Math.round(width * .5625f)
        setMeasuredDimension(width, height)
    }
}
