package com.android.sergeyfitis.geektalksdemo.models;

import android.support.annotation.Nullable;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class Picture {
    /**
     * is_silhouette : false
     * url : https://scontent.xx.fbcdn.net/hprofile-xpt1/v/t1.0-1/p50x50/11425068_853287351422665_8065006584524497776_n.png?oh=979eb1a3215944964a46e0b159e8558d&oe=56ACA9F8
     */

    private PictureData data;

    public void setData(PictureData data) {
        this.data = data;
    }

    public PictureData getData() {
        return data;
    }

    public static class PictureData {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
    @Nullable
    public String getImageUrl() {
        String url = null;
        if (data != null) {
            url = data.getUrl();
        }
        return url;
    }
}
