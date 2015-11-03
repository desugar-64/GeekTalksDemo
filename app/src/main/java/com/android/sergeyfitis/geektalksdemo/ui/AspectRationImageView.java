package com.android.sergeyfitis.geektalksdemo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Serhii Yaremych on 02.11.2015.
 */
public class AspectRationImageView extends ImageView {
    private static final float PHOTO_ASPECT_RATIO = 1.6f; // 16:10

    public AspectRationImageView(Context context) {
        super(context);
    }

    public AspectRationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AspectRationImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int targetCoverHeight = (int) (width / PHOTO_ASPECT_RATIO);
        int coverHSpec = MeasureSpec.makeMeasureSpec(targetCoverHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, coverHSpec);
    }
}
