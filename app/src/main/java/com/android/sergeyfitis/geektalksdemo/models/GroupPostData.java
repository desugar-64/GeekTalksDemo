package com.android.sergeyfitis.geektalksdemo.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Serhii Yaremych on 02.11.2015.
 */
public class GroupPostData {
    private String caption;
    private String description;
    @SerializedName("full_picture")
    private String fullPicture;
    private String id;
    @SerializedName("created_time")
    private Date createdTime;


    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
