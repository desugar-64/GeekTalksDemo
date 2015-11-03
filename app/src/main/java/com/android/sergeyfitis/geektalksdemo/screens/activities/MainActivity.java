package com.android.sergeyfitis.geektalksdemo.screens.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.api.facebook.FBRequests;
import com.android.sergeyfitis.geektalksdemo.helpers.Prefs;
import com.android.sergeyfitis.geektalksdemo.helpers.RxUtils;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;
import com.android.sergeyfitis.geektalksdemo.models.Group;
import com.android.sergeyfitis.geektalksdemo.models.GroupResponse;
import com.android.sergeyfitis.geektalksdemo.models.User;
import com.android.sergeyfitis.geektalksdemo.ui.adapters.GroupsAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.rv_likes)
    RecyclerView rvLikes;
    @Bind(R.id.cl_main_root)
    CoordinatorLayout clMainRoot;
    @Bind(R.id.nv_drawer)
    NavigationView nvDrawer;
    ImageView ivUserCover;
    TextView tvUserName;
    @Bind(R.id.v_main_bg)
    View vMainBg;
    @Bind(R.id.app_bar_main)
    AppBarLayout appBarMain;
    @Bind(R.id.sc_pre_lollipop_enabler)
    SwitchCompat scPreLollipopEnabler;

    @BindColor(R.color.cerulean)
    int lollipopColor;

    private int scrolledDistance = 0;
    private float toolBarAlphaOffset;
    private boolean isAppBarAnimating = true;
    private int preLollipopColor = Color.parseColor("#FFAA00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Magic android api downgrader
        scPreLollipopEnabler.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? View.GONE : View.VISIBLE);
        scPreLollipopEnabler.setChecked(Prefs.isPreLollipopApiEnabled());
        if (scPreLollipopEnabler.isChecked()) {
            vMainBg.setBackgroundColor(preLollipopColor);
        }

        prepareForStartupAnimation();
        rvLikes.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        toolBarAlphaOffset = getResources().getDimensionPixelOffset(R.dimen.toolbar_height) / 3f;
        // Setup ui
        View navigationHeader = getLayoutInflater().inflate(R.layout.drawer_header_layout, nvDrawer, false);
        tvUserName = ButterKnife.findById(navigationHeader, R.id.tv_drawer_user);
        ivUserCover = ButterKnife.findById(navigationHeader, R.id.iv_drawe_user_avatar);
        nvDrawer.addHeaderView(navigationHeader);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }

        // Sync data with Facebook
        fetchGroups();
        fetchUser();
        rvLikes.addOnScrollListener(onScrollListener);
    }

    @OnCheckedChanged(R.id.sc_pre_lollipop_enabler)
    void onPreLollipopApiCheckedChanged(boolean isChecked) {
        if (isChecked != Prefs.isPreLollipopApiEnabled()) {
            Prefs.setPreLollipopApiEnabled(isChecked);
            vMainBg.setBackgroundColor(isChecked ? preLollipopColor : lollipopColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(vMainBg,
                        scPreLollipopEnabler.getLeft() + scPreLollipopEnabler.getWidth() / 2,
                        toolbar.getHeight(),
                        scPreLollipopEnabler.getHeight() / 2,
                        vMainBg.getWidth())
                        .setDuration(defaultAnimationDuration)
                        .start();
            }
        }
    }

    private void prepareForStartupAnimation() {
        drawerLayout.getChildAt(0)
                .getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        drawerLayout.getChildAt(0)
                                .getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        appBarStartupAnimation();
                        return true;
                    }
                });
    }

    private void appBarStartupAnimation() {
        vMainBg.setTranslationY(-vMainBg.getHeight() / 2);
        appBarMain.setAlpha(0f);
        ViewCompat.animate(vMainBg)
                .translationY(0f)
                .setDuration(defaultAnimationDuration)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .withEndAction(() -> ViewCompat.animate(appBarMain)
                        .alpha(1f)
                        .setDuration(defaultAnimationDuration)
                        .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                        .withEndAction(() -> isAppBarAnimating = false)
                        .start())
                .start();
    }

    private void fetchUser() {
        subscribeIfInternetAvailable(
                FBRequests.getDefault()
                        .me()
                        .compose(RxUtils.applySchedulers())
                        .filter(user -> user != null)
                        .subscribe(this::updateDrawer, this::showError)
        );
    }

    private void fetchGroups() {
        subscribeIfInternetAvailable(
                FBRequests.getDefault()
                        .groups()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(response -> response != null && response.getData() != null)
                        .map(GroupResponse::getData)
                        .subscribe(this::updateUI, this::showError)
        );
    }

    private void updateUI(@NonNull List<Group> groups) {
        listUpdateAnimation();
        GroupsAdapter adapter = new GroupsAdapter(groups);
        rvLikes.setAdapter(adapter);
        adapter.setOnItemClickListener(onItemClickListener);
    }

    private final GroupsAdapter.OnItemClickListener onItemClickListener = MainActivity.this::openDetails;

    private void openDetails(Group group, int[] uiColors, Pair<View, String>... args) {
        Intent intent = new Intent(this, GroupDetailsActivity.class);
        intent.putExtra(GroupDetailsActivity.KEY_GROUP_NAME, group.getName());
        intent.putExtra(GroupDetailsActivity.KEY_GROUP_COVER_URL, group.getPicture().getImageUrl());
        intent.putExtra(GroupDetailsActivity.KEY_GROUP_ID, group.getId());
        intent.putExtra(GroupDetailsActivity.KEY_GROUP_ABOUT, group.getAbout());
        intent.putExtra(GroupDetailsActivity.KEY_UI_COLORS, uiColors);
        ActivityOptionsCompat optionsCompat;
        if (Utils.hasLollipop()) {
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, args);
        } else {
            optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_from_right, R.anim.do_nothing);
        }
        startActivity(intent, optionsCompat.toBundle());
    }

    private void listUpdateAnimation() {
        rvLikes.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        rvLikes.getViewTreeObserver().removeOnPreDrawListener(this);
                        listStartupAnimation();
                        return true;
                    }
                });
    }

    private void listStartupAnimation() {
        int listChildrenCount = rvLikes.getChildCount();
        int animationDelay = 75;
        for (int i = 0; i < listChildrenCount; i++) {
            View child = rvLikes.getChildAt(i);
            ViewCompat.setTranslationY(child, child.getHeight() * (i + 2));
            ViewCompat.animate(child)
                    .translationY(0f)
                    .setDuration(defaultAnimationDuration)
                    .setStartDelay(animationDelay * i)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .start();
        }
    }

    private void updateDrawer(User user) {
        String imageUrl = user.getPicture().getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            subscribeIfInternetAvailable(
                    RxUtils.generatePalette(imageUrl, MainActivity.this, false)
                            .subscribe(this::onPaletteGenerated)
            );
        }
        tvUserName.setText(user.getName());
    }

    private void onPaletteGenerated(Pair<Palette, Bitmap> paletteBitmapPair) {
        List<Palette.Swatch> swatches = paletteBitmapPair.first.getSwatches();
        if (!swatches.isEmpty()) {
            Palette.Swatch swatch = Utils.findSwatchByMostUsedColor(swatches);
            tvUserName.setBackgroundColor(swatch.getRgb());
            tvUserName.setTextColor(swatch.getBodyTextColor());
            ivUserCover.setImageBitmap(paletteBitmapPair.second);
        }
    }

    private void showError(Throwable throwable) {
        showMessage(clMainRoot, throwable.getMessage());
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrolledDistance += dy;
            if (isAppBarAnimating) {
                return;
            }
            animateBackgroundView(scrolledDistance);
            if (scrolledDistance < toolBarAlphaOffset) {
                float alpha = 1f - (scrolledDistance / toolBarAlphaOffset);
                appBarMain.setAlpha(alpha);
                appBarMain.setTranslationY(-scrolledDistance);
            } else {
                if (appBarMain.getAlpha() != 0f) {
                    appBarMain.setAlpha(0f);
                }
            }
        }
    };

    private void animateBackgroundView(int scrolledDistance) {
        if (scrolledDistance <= vMainBg.getHeight()) {
            vMainBg.setTranslationY(-scrolledDistance / 2); // Add some parallax effect to scrolling
        } else {
            vMainBg.setTranslationY(-(vMainBg.getHeight() / 2));
        }
    }

}
