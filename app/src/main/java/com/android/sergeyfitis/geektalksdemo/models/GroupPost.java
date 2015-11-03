package com.android.sergeyfitis.geektalksdemo.models;

import java.util.List;

/**
 * Created by Serhii Yaremych on 02.11.2015.
 */
public class GroupPost {

    /**
     * caption : Geek Talks: Android Animations
     * description : Text
     * full_picture : https://scontent.xx.fbcdn.net/hphotos-xat1/v/t1.0-9/c140.0.360.360/12122584_920314928053240_963134818973711609_n.jpg?oh=98ddee0932eddfdc2c1222f0fffbca20&oe=56CEE31F
     * id : 836927043058696_920329458051787
     */

    private List<GroupPostData> data;

    public void setData(List<GroupPostData> data) {
        this.data = data;
    }

    public List<GroupPostData> getData() {
        return data;
    }
}
