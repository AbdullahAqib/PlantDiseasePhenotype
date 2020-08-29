package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    public static String AppId = "4dfbee4b454ab2aa20215eb57c73c7a1";
    public static String lat = "35";
    public static String lon = "139";

    TextView txt_temperature, txt_humidity, txt_pressure;
    ImageView weather_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txt_temperature = findViewById(R.id.txt_temperature);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_pressure = findViewById(R.id.txt_pressure);
        weather_icon = findViewById(R.id.weather_icon);

        BottomNavigationView navbar;

        navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_home);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_camera:
                        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_prediction:
                        startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_blogs:
                        startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        getCurrentData();

    }

    void getCurrentData() {
        Call<WeatherResponse> call = WeatherAPI.getWeatherService().getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

//                    String stringBuilder = "Country: " +
//                            weatherResponse.sys.country +
//                            "\n" +
//                            "Temperature: " +
//                            weatherResponse.main.temp +
//                            "\n" +
//                            "Temperature(Min): " +
//                            weatherResponse.main.temp_min +
//                            "\n" +
//                            "Temperature(Max): " +
//                            weatherResponse.main.temp_max +
//                            "\n" +
//                            "Humidity: " +
//                            weatherResponse.main.humidity +
//                            "\n" +
//                            "Pressure: " +
//                            weatherResponse.main.pressure;

                    Glide.with(getApplicationContext()).load("http://openweathermap.org/img/w/" + weatherResponse.weather.get(0).icon + ".png").into(weather_icon);
                    txt_temperature.setText("Temp: "+String.valueOf(weatherResponse.main.temp)+"°");
                    txt_humidity.setText("Hum: "+String.valueOf(weatherResponse.main.humidity));
                    txt_pressure.setText("Pres: "+String.valueOf(weatherResponse.main.pressure));

                }
            }


            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                t.getMessage();
            }
        });
    }

}