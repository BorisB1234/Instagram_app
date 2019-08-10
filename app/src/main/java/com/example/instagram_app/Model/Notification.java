package com.example.instagram_app.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;
@Entity
public class Notification implements Serializable {
    @PrimaryKey
    @NonNull
    private String userid;

    private String text;
    private String postid;
    private boolean ispost;

    public Notification(String userid, String text, String postid, boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "userid='" + userid + '\'' +
                ", text='" + text + '\'' +
                ", postid='" + postid + '\'' +
                ", ispost=" + ispost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(userid, that.userid) &&
                Objects.equals(postid, that.postid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, postid);
    }
}
