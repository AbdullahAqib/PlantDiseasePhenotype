package com.example.plantdiseasephenotype.utils;

public class Feedback {
    private String feedback;
    private String userId;

    public Feedback(String feedback, String userId) {
        this.feedback = feedback;
        this.userId = userId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
