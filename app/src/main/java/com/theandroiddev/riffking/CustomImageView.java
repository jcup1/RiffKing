package com.theandroiddev.riffking;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by jakub on 04.10.17.
 */

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {
    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();

        //force a 16:9 aspect ratio
        int height = Math.round(width * .5625f);
        setMeasuredDimension(width, height);
    }
}
