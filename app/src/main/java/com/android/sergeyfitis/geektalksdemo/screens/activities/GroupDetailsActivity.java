package com.android.sergeyfitis.geektalksdemo.screens.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.api.facebook.FBRequests;
import com.android.sergeyfitis.geektalksdemo.helpers.RxUtils;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;
import com.android.sergeyfitis.geektalksdemo.models.Group;
import com.android.sergeyfitis.geektalksdemo.models.GroupPostData;
import com.android.sergeyfitis.geektalksdemo.ui.adapters.GroupPostsAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_GROUP_NAME = "group_name";
    public static final String KEY_GROUP_COVER_URL = "group_cover_url";
    public static final String KEY_GROUP_ABOUT = "group_about";
    public static final String KEY_UI_COLORS = "ui_colors";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.5f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    @Bind(R.id.cl_details_root)
    CoordinatorLayout clDetailsRoot;
    @Bind(R.id.rv_posts)
    RecyclerView rvPosts;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Bind(R.id.iv_detail_main_cover)
    ImageView ivDetailMainCover;
    @Bind(R.id.tv_secondary_group_title)
    TextView tvSecondaryGroupTitle;
    @Bind(R.id.ll_details_title_container)
    LinearLayout llDetailsTitleContainer;
    @Bind(R.id.fl_main_details_title_container)
    FrameLayout flMainDetailsTitleContainer;
    @Bind(R.id.tv_first_group_title)
    TextView tvFirstGroupTitle;
    @Bind(R.id.tl_details)
    Toolbar tlDetails;
    @Bind(R.id.ctl_details)
    CollapsingToolbarLayout ctlDetails;
    @Bind(R.id.ab_details)
    AppBarLayout abDetails;
    @Bind(R.id.iv_details_mini_cover)
    ImageView ivDetailsMiniCover;
    @Bind(R.id.tv_group_details)
    TextView tvGroupDetails;


    private String groupName;
    private String groupId;
    private String groupUrl;
    private String groupAbout;
    private int[] uiColors;

    private int toolbarBackgroundColor;
    private boolean toolbarBgVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        getExtras();
        ButterKnife.bind(this);
        Utils.loadImage(groupUrl, ivDetailMainCover);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(tlDetails);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("");
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        abDetails.addOnOffsetChangedListener(this);

        rvPosts.postDelayed(GroupDetailsActivity.this::fetchGroupDetails, 510);
        updateUI(null);

    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getIntent();
    }

    private void updateUI(@Nullable Group group) {
        Log.d(TAG, "updateUI");
        long start = System.currentTimeMillis();
        if (group != null) {
            groupName = group.getName();
            groupName = group.getName();
            groupAbout = group.getAbout();
            if (group.getPicture().getImageUrl() != null)
                groupUrl = group.getPicture().getImageUrl();
            groupId = group.getId();
            listUpdateAnimation();
            GroupPostsAdapter adapter = new GroupPostsAdapter(group.getPosts().getData());
            rvPosts.post(() -> rvPosts.setAdapter(adapter));
            adapter.setOnPostItemClick(onPostItemClick);
            subscribeIfInternetAvailable(
                    RxUtils.generatePalette(groupUrl, this, true)
                            .subscribe(this::updateCover)
            );

        } else {
            llDetailsTitleContainer.setBackgroundColor(uiColors[0]);
            tvFirstGroupTitle.setTextColor(uiColors[1]);
            tvSecondaryGroupTitle.setTextColor(uiColors[1]);
            tvGroupDetails.setTextColor(uiColors[2]);
            tlDetails.setTitleTextColor(uiColors[1]);
        }
        tvFirstGroupTitle.setText(groupName);
        tvSecondaryGroupTitle.setText(groupName);
        tvGroupDetails.setText(groupAbout);
        long end = System.currentTimeMillis() - start;
        Log.d(TAG, "updateUI ms: " + end);
    }

    private final GroupPostsAdapter.OnPostItemClick onPostItemClick = GroupDetailsActivity.this::openPostDetails;

    private void openPostDetails(GroupPostData postData, Pair<View, String>[] args) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.KEY_POST_IMAGE_URL, postData.getFullPicture());
        intent.putExtra(PostDetailsActivity.KEY_POST_TEXT, postData.getDescription());
        ActivityOptionsCompat optionsCompat;
        if (Utils.hasLollipop()) {
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, args);
        } else {
            optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_from_right, R.anim.do_nothing);
        }
        startActivity(intent, optionsCompat.toBundle());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateCover(Pair<Palette, Bitmap> bitmapPalettePair) {

        long start = System.currentTimeMillis();
        ivDetailsMiniCover.setImageBitmap(bitmapPalettePair.second);
        Palette.Swatch swatch = Utils.findSwatchByMostUsedColor(bitmapPalettePair.first.getSwatches());
        llDetailsTitleContainer.setBackgroundColor(swatch.getRgb());
        tvFirstGroupTitle.setTextColor(swatch.getTitleTextColor());
        tvSecondaryGroupTitle.setTextColor(swatch.getTitleTextColor());
        tvGroupDetails.setTextColor(swatch.getBodyTextColor());
        tlDetails.setTitleTextColor(swatch.getTitleTextColor());
        toolbarBackgroundColor = swatch.getRgb();
        if (Utils.api21()) {
            getWindow().setStatusBarColor(swatch.getRgb());
        }
        long end = System.currentTimeMillis() - start;
        Log.d(TAG, "updateCover ms: " + end);
    }

    private void fetchGroupDetails() {
        subscribeIfInternetAvailable(
                FBRequests.getDefault()
                        .groupDetails(groupId)
                        .compose(RxUtils.applySchedulers())
                        .filter(group -> group != null)
                        .subscribe(this::updateUI, this::showError)
        );
    }

    private void listUpdateAnimation() {
        rvPosts.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rvPosts.getViewTreeObserver().removeOnPreDrawListener(this);
                listStartupAnimation();
                return true;
            }
        });
    }

    private void listStartupAnimation() {
        int listChildrenCount = rvPosts.getChildCount();
        int animationDelay = 75;
        for (int i = 0; i < listChildrenCount; i++) {
            View child = rvPosts.getChildAt(i);
            ViewCompat.setTranslationY(child, child.getHeight() / 2);
            ViewCompat.animate(child)
                    .translationY(0f)
                    .setDuration(defaultAnimationDuration)
                    .setStartDelay(animationDelay * i)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .start();
        }
    }

    private void showError(Throwable throwable) {
        showMessage(clDetailsRoot, throwable.getMessage());
    }


    private void getExtras() {
        groupName = getIntent().getStringExtra(KEY_GROUP_NAME);
        groupId = getIntent().getStringExtra(KEY_GROUP_ID);
        groupUrl = getIntent().getStringExtra(KEY_GROUP_COVER_URL);
        groupAbout = getIntent().getStringExtra(KEY_GROUP_ABOUT);
        uiColors = getIntent().getIntArrayExtra(KEY_UI_COLORS);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

//        Log.d(TAG, "percentage: " + percentage);
        handleAlphaOnTitle(percentage);
//        handleToolbarTitleVisibility(percentage);
    }


    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(flMainDetailsTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(flMainDetailsTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
        handleToolbarAndSystemUIBackgroundVisibility((1f - percentage) <= 0.5f);
    }

    private void handleToolbarAndSystemUIBackgroundVisibility(boolean visible) {
        int colorFrom = visible ? Color.TRANSPARENT : toolbarBackgroundColor;
        int colorTo = visible ? toolbarBackgroundColor : Color.TRANSPARENT;
        if (visible) {
            if (!toolbarBgVisible) {
                ObjectAnimator.ofObject(tlDetails, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo)
                        .setDuration(defaultAnimationDuration)
                        .start();
//                animateSystemUIColors(colorFrom, colorTo);
                toolbarBgVisible = true;
            }
        } else {
            if (toolbarBgVisible) {
//                animateSystemUIColors(colorFrom, colorTo);
                ObjectAnimator.ofObject(tlDetails, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo)
                        .setDuration(defaultAnimationDuration)
                        .start();
                toolbarBgVisible = false;
            }
        }
    }

    private void animateSystemUIColors(int colorFrom, int colorTo) {
        if (Utils.hasLollipop()) {
            ValueAnimator valueAnimator = ObjectAnimator.ofArgb(colorFrom, colorTo);
            valueAnimator
                    .setDuration(defaultAnimationDuration)
                    .addUpdateListener(animation -> {
                        int color = (int) animation.getAnimatedValue();
                        getWindow().setStatusBarColor(color);
                    });
            valueAnimator.start();
        }
    }


    public void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);


        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}

