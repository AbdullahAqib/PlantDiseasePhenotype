package com.example.plantdiseasephenotype.models;

import com.google.gson.annotations.SerializedName;

public class InferenceResponse {
    @SerializedName("class")
    public String class_;

    public InferenceResponse(String class_) {
        this.class_ = class_;
    }
}
