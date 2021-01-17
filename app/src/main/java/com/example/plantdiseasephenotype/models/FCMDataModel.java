package com.example.plantdiseasephenotype.models;

public class FCMDataModel {

    private String postId;

    public FCMDataModel(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
