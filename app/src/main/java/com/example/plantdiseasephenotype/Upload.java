package com.example.plantdiseasephenotype;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Upload implements Serializable {
    private String title;
    private String imageUrl;
    private String description;
    private String userId;
    private String userName;
    private long commentCount;
    private long timestamp;
    private String key;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String title, String imageUrl, String userId, String userName) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
    }

    public Upload(String title, String imageUrl, String description, String userId, String userName) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
    }

    public Upload(String title, String imageUrl, String description, String userId, String userName, long timestamp) {
        if (title.trim().equals("")) {
            title = "No Title";
        }

        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public void increaseCommentCount(){
        commentCount++;
    }

    public String getUploadDate(){
        Date date = new Date(timestamp);
        String dateTimePattern = "MMM dd, yyyy EEE h:mm a";
        return new SimpleDateFormat(dateTimePattern).format(date);
    }
}
