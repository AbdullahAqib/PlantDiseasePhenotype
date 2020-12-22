package com.example.plantdiseasephenotype.network;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public class DeepLearningAPI {
    public interface DeepLearningService {
        @Multipart
        @POST("api/infer")
        Call<InferenceResponse> getInference(@Part MultipartBody.Part img);

        @Multipart
        @POST("api/salmap")
        Call<ResponseBody> getSaliencyMap(@Part MultipartBody.Part img);
    }

    public static String baseUrl = "http://192.168.10.2:5000/";

    public static DeepLearningService getDeepLearningService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(DeepLearningService.class);
    }
}
