package com.android.sergeyfitis.geektalksdemo.api;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public final class C {

    private C() {
    }

    public static final String FB_BASE_URL = "https://graph.facebook.com/v2.5/";

    // User
    public static final String GET_MY_PROFILE = "me?fields=id,name,picture.height(400).width(400)";

    // Groups
    public static final String GET_GROUPS = "me/likes?fields=id,name,likes,picture.height(500).width(500),about";
//    public static final String GROUP_DETAILS = "{group_id}?fields=id,name,about,picture.width(400).height(400),posts{caption,description,full_picture,,created_time}";
    public static final String GROUP_DETAILS = "{group_id}?fields=id,name,about,picture.width(400).height(400),posts%7Bcaption%2Cdescription%2Cfull_picture%2Ccreated_time%7D";
}


