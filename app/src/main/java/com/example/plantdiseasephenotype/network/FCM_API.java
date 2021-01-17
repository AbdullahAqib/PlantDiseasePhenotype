package com.example.plantdiseasephenotype.network;

import com.example.plantdiseasephenotype.models.RequestNotification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class FCM_API {

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static FCM_API.FCMService FCMService = null;

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface FCMService {
        @Headers({"Authorization: key=AAAAh6Q5LoA:APA91bEbmeMyDLMXxASq9pC00HRwyeS3_3AtY4-JX81Stjuqz7Lc6X5l4fSotvt0GMKtMcBbJAiVYoipv5ZQJcHwN6e-NxR2wAGMlfMXgS0rr6mGC8O8KKt9z5TLlN_ujlkjzWEth_XA",
                "Content-Type:application/json"})
        @POST("fcm/send")
        Call<ResponseBody> sendNotification(@Body RequestNotification requestNotificaton);
    }
}
