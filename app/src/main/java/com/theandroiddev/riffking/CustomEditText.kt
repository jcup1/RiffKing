package com.theandroiddev.riffking

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent

/**
 * Created by jakub on 03.10.17.
 */

class CustomEditText : AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attribute_set: AttributeSet) : super(context, attribute_set)

    constructor(context: Context, attribute_set: AttributeSet, def_style_attribute: Int) : super(context, attribute_set, def_style_attribute)

    override fun onKeyPreIme(key_code: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)
            this.clearFocus()

        return super.onKeyPreIme(key_code, event)
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }
        return super.dispatchKeyEventPreIme(event)
    }

}
