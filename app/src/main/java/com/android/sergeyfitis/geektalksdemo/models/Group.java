package com.android.sergeyfitis.geektalksdemo.models;

import java.util.List;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class Group {

    private String id;
    private String name;
    private Picture picture;
    private String about;
    private int likes;
    private GroupPosts posts;


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Picture getPicture() {
        return picture;
    }

    public String getAbout() {
        return about;
    }

    public int getLikes() {
        return likes;
    }

    public GroupPosts getPosts() {
        return posts;
    }

    public void setPosts(GroupPosts posts) {
        this.posts = posts;
    }
}
