package com.example.android.likeeatapplication.Model;

/**
 * Created by Leonardo on 4/21/2018.
 */

public class Comment {
    //model untuk class comment
    //attribut2 apa aja yang ada di class comment
    String id;
    String username;
    String comment;
    Long timestamp;
    String profile_pic;

    //construcor kosong
    public Comment() {
    }

    public Comment(String id,String username, String comment, Long timestamp, String profile_pic) {
        this.id=id;
        this.username = username;
        this.comment = comment;
        this.timestamp=timestamp;
        this.profile_pic=profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    //method getter
    public Long getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }
}