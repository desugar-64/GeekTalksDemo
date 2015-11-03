package com.android.sergeyfitis.geektalksdemo.screens.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final FastOutSlowInInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    protected int defaultAnimationDuration;

    protected final String TAG = this.getClass().getSimpleName();
    protected final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!Utils.hasLollipop()) {
            finish();
            overridePendingTransition(0, R.anim.slide_out_to_right);
        } else {
            supportFinishAfterTransition();
        }
    }

    protected void subscribeIfInternetAvailable(Subscription subscription) {
        if (Utils.hasInternet(this)) {
            subscriptions.add(subscription);
        }
    }

    protected void showMessage(@NonNull View anchor, String message) {
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT).show();
    }
}
