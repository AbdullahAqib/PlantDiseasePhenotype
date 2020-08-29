package com.example.plantdiseasephenotype;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherAPI {

    public static String url = "http://api.openweathermap.org/";

    public static WeatherService weatherService = null;

    public static WeatherService getWeatherService(){

        if(weatherService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            weatherService = retrofit.create(WeatherService.class);
        }

        return weatherService;
    }

    public interface WeatherService {
        @GET("data/2.5/weather?")
        Call<WeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String app_id);
    }
}
