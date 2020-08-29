package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navbar;

        navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_profile);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_camera:
                        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_prediction:
                        startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_blogs:
                        startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.nav_profile:
                        return true;
                }
                return false;
            }
        });

    }

}
