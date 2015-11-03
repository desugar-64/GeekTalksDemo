package com.android.sergeyfitis.geektalksdemo.helpers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;

import java.util.Collections;
import java.util.List;

/**
 * Created by serge on 31.10.2015.
 */
public class Utils {

    private Utils() {
    }

    public static boolean hasInternet(Context context) {
        if (context == null) {
            return false;
        }

        NetworkInfo info = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isConnectedOrConnecting()) {
            return false;
        }
        if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to disable
            // internet while roaming, just return false
            return true;
        }
        return true;
    }

    public static void saveFbAuth(@NonNull AccessToken accessToken) {
        Prefs.setFbAccessToken(accessToken.getToken());
        Prefs.setFbUserId(accessToken.getUserId());
    }

    public static void loadImage(@NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(new ColorDrawable(ContextCompat.getColor(imageView.getContext(), R.color.cerulean)))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }

    public static Palette.Swatch findSwatchByMostUsedColor(List<Palette.Swatch> swatches) {
        return Collections.max(swatches, (lhs, rhs) -> lhs.getPopulation() > rhs.getPopulation() ? 1 :
                lhs.getPopulation() == rhs.getPopulation() ? 0 : -1);
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !Prefs.isPreLollipopApiEnabled();
    }

    public static boolean api21() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
