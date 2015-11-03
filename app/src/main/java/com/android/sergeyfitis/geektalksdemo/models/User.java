package com.android.sergeyfitis.geektalksdemo.models;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class User {
    private String id;
    private String name;
    private Picture picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
