package com.example.plantdiseasephenotype.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String userId;
    private String commentId;
    private String commentBody;
    private String userName;
    private long timestamp;

    public Comment(){}

    public Comment(String userId, String commentBody, String userName) {
        this.userId = userId;
        this.commentBody = commentBody;
        this.userName = userName;
    }

    public Comment(String userId, String commentBody, String userName, long timestamp) {
        this.userId = userId;
        this.commentBody = commentBody;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
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

    public String getUploadDate(){
        Date date = new Date(timestamp);
        String dateTimePattern = "MMM dd, yyyy EEE h:mm a";
        return new SimpleDateFormat(dateTimePattern).format(date);
    }
}
