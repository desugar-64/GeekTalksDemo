package com.android.sergeyfitis.geektalksdemo.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class Prefs {
    private static final String FB_ACCESS_TOKEN = "fb_token";
    private static final String FB_USER_NAME = "fb_user_name";
    private static final String FB_USER_ID = "fb_user_id";
    private static final String KEY_DOWNGRADE_API  = "key_downgrade_api";


    private static SharedPreferences prefs = null;

    private Prefs() {
    }

    public static void init(Application application) {
        if (prefs == null) {
            prefs = application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
        }
    }

    public static void setFbAccessToken(String accessToken) {
        updatePrefs(FB_ACCESS_TOKEN, accessToken);
    }

    public static void setPreLollipopApiEnabled(boolean enabled) {
        updatePrefs(KEY_DOWNGRADE_API, enabled);
    }

    public static boolean isPreLollipopApiEnabled() {
        return prefs.getBoolean(KEY_DOWNGRADE_API,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP);
    }

    public static void setFbUserName(String uName) {
        updatePrefs(FB_USER_NAME, uName);
    }

    public static void setFbUserId(String uId) {
        updatePrefs(FB_USER_ID, uId);
    }

    public static String getFbAccessToken() {
        return prefs.getString(FB_ACCESS_TOKEN, null);
    }

    public static String getFbUserName() {
        return prefs.getString(FB_USER_NAME, null);
    }

    public static String getFbUserId() {
        return prefs.getString(FB_USER_ID, null);
    }

    private static void updatePrefs(@NonNull String key, String value) {
        prefs.edit()
                .putString(key, value)
                .apply();
    }
    private static void updatePrefs(@NonNull String key, int value) {
        prefs.edit()
                .putInt(key, value)
                .apply();
    }
    private static void updatePrefs(@NonNull String key, boolean value) {
        prefs.edit()
                .putBoolean(key, value)
                .apply();
    }
}
