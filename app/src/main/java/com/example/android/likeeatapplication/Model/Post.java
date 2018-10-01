package com.example.android.likeeatapplication.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Leonardo on 4/6/2018.
 */

public class Post {
    private String id;
    private String userID;
    private String username;
    private String titlePost;
    private String post;
    private int report;
    private int like;
    private String imagePost;
    private String alamat;
    public Long timestamp;
    private HashMap<String, Comment> comment;

    public Post() {
    }

    public Post(String id, String userID, String username, String mImagePost, String titlePost, String post,String alamat, Long timestamp,
                HashMap<String, Comment> comment, int report, int like) {
        this.id = id;
        this.username = username;
        this.imagePost = mImagePost;
        this.titlePost = titlePost;
        this.post = post;
        this.userID = userID;
        this.timestamp = timestamp;
        this.comment = comment;
        this.alamat = alamat;
        this.report = report;
        this.like = like;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public HashMap<String, Comment> getComment() {
        return comment;
    }

    public void setComment(HashMap<String, Comment> comment) {
        this.comment = comment;
    }

    public String getImagePost() {
        return imagePost;
    }

    public String getUserID() {
        return userID;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTitlePost() {
        return titlePost;
    }

    public String getPost() {
        return post;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getAlamat(){
        return alamat;
    }
}
