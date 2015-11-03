package com.android.sergeyfitis.geektalksdemo.ui.behavior;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.sergeyfitis.geektalksdemo.R;

/**
 * Created by Serhii Yaremych on 01.11.2015.
 */
public class CoordinatorDetailsBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private int statusBarHeight;
    private float mStartToolbarPosition;
    private boolean isToolbarVisible = false;



    public CoordinatorDetailsBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        statusBarHeight = context.getResources().getDimensionPixelOffset(R.dimen.status_bar_height);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        if (mStartToolbarPosition == 0) {
            mStartToolbarPosition = dependency.getY() + (dependency.getHeight() / 2);
        }
        Toolbar toolbar = (Toolbar) dependency;
        final int maxScrollDistance = (int) (mStartToolbarPosition - statusBarHeight);
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;
        if (expandedPercentageFactor > 1f) return false;
        if (expandedPercentageFactor <= 0.125f) {
            animateVisibility(true, child, toolbar);
        } else {
            animateVisibility(false, child, toolbar);
        }
        Log.d(CoordinatorDetailsBehavior.class.getSimpleName(), "CoordinatorDetailsBehavior expandedPercentageFactor: " + expandedPercentageFactor);
        return true;
    }

    private void animateVisibility(boolean visible, ImageView child, Toolbar toolbar) {
        if (visible && !isToolbarVisible) {
            toolbar.getChildAt(0).setVisibility(View.VISIBLE);
            toolbar.getChildAt(0).animate()
                    .alpha(1f)
                    .setDuration(100)
                    .start();
            child.setVisibility(View.VISIBLE);
            ViewCompat.animate(child)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start();
            isToolbarVisible = true;
        } else if (!visible && isToolbarVisible) {
            toolbar.getChildAt(0).animate()
                    .alpha(0f)
                    .setDuration(100)
                    .start();
            ViewCompat.animate(child)
                    .scaleX(0.01f)
                    .scaleY(0.01f)
                    .setDuration(100)
                    .withEndAction(() -> child.setVisibility(View.GONE))
                    .start();
            isToolbarVisible = false;
        }

    }

}
