package com.android.sergeyfitis.geektalksdemo.screens;

import android.app.Application;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.Prefs;
import com.facebook.FacebookSdk;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        Prefs.init(this);
    }
}
