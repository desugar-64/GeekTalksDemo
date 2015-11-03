package com.android.sergeyfitis.geektalksdemo.screens.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.RxUtils;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostDetailsActivity extends BaseActivity {

    public static final String KEY_POST_IMAGE_URL = "post_url";
    public static final String KEY_POST_TEXT = "post_text";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_post_details)
    ImageView ivPostDetails;
    @Bind(R.id.tv_post_details)
    TextView tvPostDetails;
    @Bind(R.id.nsv_post_text)
    NestedScrollView nsvPostText;
    @Bind(R.id.cl_post_root)
    CoordinatorLayout clPostRoot;

    private String postImageUrl;
    private String postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("");
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        getExtras();
        Utils.loadImage(postImageUrl, ivPostDetails);
        tvPostDetails.setText(postText);
        updateUiColors();
        prepareForStartupAnimation();
    }

    private void prepareForStartupAnimation() {
        nsvPostText.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        nsvPostText.getViewTreeObserver().removeOnPreDrawListener(this);
                        startUpAnimation();
                        return true;
                    }
                }
        );
    }

    private void startUpAnimation() {
        nsvPostText.setTranslationY(nsvPostText.getHeight());
        ViewCompat.animate(nsvPostText)
                .setDuration(defaultAnimationDuration)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .translationY(0f)
                .start();
        ViewCompat.setAlpha(tvPostDetails, 0f);
        ViewCompat.animate(tvPostDetails)
                .setDuration(defaultAnimationDuration / 2)
                .setStartDelay(defaultAnimationDuration / 2)
                .alpha(1f)
                .start();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getIntent();
    }

    private void updateUiColors() {
        supportPostponeEnterTransition();
        RxUtils.generatePalette(postImageUrl, this, false)
                .subscribe(this::onPaletteGenerated, this::showError);
    }

    private void onPaletteGenerated(Pair<Palette, Bitmap> paletteBitmapPair) {
        Palette.Swatch swatch = Utils.findSwatchByMostUsedColor(paletteBitmapPair.first.getSwatches());
        nsvPostText.setBackgroundColor(swatch.getRgb());
        tvPostDetails.setTextColor(swatch.getBodyTextColor());
        if (Utils.api21()) {
            getWindow().setStatusBarColor(swatch.getRgb());
        }
        supportStartPostponedEnterTransition();
    }

    private void getExtras() {
        postImageUrl = getIntent().getStringExtra(KEY_POST_IMAGE_URL);
        postText = getIntent().getStringExtra(KEY_POST_TEXT);
    }

    private void showError(Throwable throwable) {
        showMessage(clPostRoot, throwable.getMessage());
    }

}
