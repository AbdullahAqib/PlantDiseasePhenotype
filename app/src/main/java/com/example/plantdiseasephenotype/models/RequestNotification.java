package com.example.plantdiseasephenotype.models;

import com.google.gson.annotations.SerializedName;

public class RequestNotification {

    @SerializedName("to")
    private String token;

    @SerializedName("data")
    private FCMDataModel dataModel;

    @SerializedName("notification")
    private FCMNotificationModel notificationModel;

    public FCMNotificationModel getNotificationModel() {
        return notificationModel;
    }

    public void setNotificationModel(FCMNotificationModel sendNotificationModel) {
        this.notificationModel = sendNotificationModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FCMDataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(FCMDataModel dataModel) {
        this.dataModel = dataModel;
    }
}
