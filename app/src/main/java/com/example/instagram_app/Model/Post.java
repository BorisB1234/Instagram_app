package com.example.instagram_app.Model;

import androidx.annotation.NonNull;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String gpsLatitude;
    private String gpsLongitude;

    public Post(@NonNull String postid,@NonNull String postimage, @NonNull String description,@NonNull String publisher,@NonNull String gpsLatitude,@NonNull String gpsLongitude) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description+"";
        this.publisher = publisher;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
    }

    public Post(){

    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }
}
