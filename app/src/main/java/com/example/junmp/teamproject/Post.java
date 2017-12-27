package com.example.junmp.teamproject;

import android.graphics.Bitmap;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by junmp on 2017-11-30.
 */

public class Post {
    String caption;
    String postid;
    String userId;
    String time;
    int like;
    ArrayList<String> comment;

    public Post(String caption, String postid, String userId, String time, int like, ArrayList<String> comment) {
        this.caption = caption;
        this.postid = postid;
        this.userId = userId;
        this.time = time;
        this.like = like;
        this.comment = comment;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public ArrayList<String> getComment() {
        return comment;
    }

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }
}
