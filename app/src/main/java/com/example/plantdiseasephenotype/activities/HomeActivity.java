package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.network.WeatherAPI;
import com.example.plantdiseasephenotype.network.WeatherResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    public static String AppId = "4dfbee4b454ab2aa20215eb57c73c7a1";
    public static String lat = null;
    public static String lon = null;

    TextView txt_temperature, txt_hum_pres, txt_city_name, txt_date, txt_meridiem;
    ImageView weather_icon;
    ImageButton askCommunityButton, helpOthersButton, myUploadsButton;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        askCommunityButton = findViewById(R.id.btn_ask_coummunit);
        askCommunityButton.setOnClickListener(this);
        helpOthersButton = findViewById(R.id.btn_help_others);
        helpOthersButton.setOnClickListener(this);
        myUploadsButton = findViewById(R.id.btn_my_uploads);
        myUploadsButton.setOnClickListener(this);
        txt_temperature = findViewById(R.id.txt_temperature);
        txt_hum_pres = findViewById(R.id.txt_hum_pres);
        txt_city_name = findViewById(R.id.txt_city_name);
        weather_icon = findViewById(R.id.weather_icon);
        txt_date = findViewById(R.id.txt_date);
        txt_meridiem = findViewById(R.id.txt_meridiem);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setLocation();
        setTimeAndDate();

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_home);
        navbar.setOnNavigationItemSelectedListener(this);
    }

    void setLocation(){
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            lat = String.valueOf(addresses.get(0).getLatitude());
                            lon = String.valueOf(addresses.get(0).getLongitude());
                            txt_city_name.setText(addresses.get(0).getLocality());
                            getWeatherData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    void setTimeAndDate() {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int meridiem = cal.get(Calendar.AM_PM);

        DateFormatSymbols dfs = new DateFormatSymbols();
        String weekdays[] = dfs.getWeekdays();
        String months[] = dfs.getMonths();
        String nameOfDay = weekdays[dayOfWeek];
        String nameOfMonth = months[month];
        txt_date.setText(nameOfDay + ", " + nameOfMonth + " " + dayOfMonth);

        if (meridiem == 1) {
            txt_meridiem.setText("PM");
        } else {
            txt_meridiem.setText("AM");
        }
    }


    void getWeatherData() {
        Call<WeatherResponse> call = WeatherAPI.getWeatherService(getApplicationContext()).getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    Glide.with(getApplicationContext()).load("http://openweathermap.org/img/w/" + weatherResponse.weather.get(0).icon + ".png").into(weather_icon);
                    txt_temperature.setText(String.valueOf(Math.round(weatherResponse.main.temp - 273.15)) + "°");
                    txt_hum_pres.setText(String.valueOf(weatherResponse.main.humidity) + "% / " + String.valueOf(weatherResponse.main.pressure) + " hPa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                t.getMessage();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                startActivity(new Intent(getApplicationContext(), CameraXActivity.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.nav_prediction:
                startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.nav_home:
                return true;
            case R.id.nav_blogs:
                startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_ask_coummunit:
                intent = new Intent(getApplicationContext(), AskCommunityActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_help_others:
                intent = new Intent(getApplicationContext(), ImagesActivity.class);
                intent.putExtra("id", view.getId());
                startActivity(intent);
                break;
            case R.id.btn_my_uploads:
                intent = new Intent(getApplicationContext(), ImagesActivity.class);
                intent.putExtra("id", view.getId());
                startActivity(intent);
                break;
        }
    }
}