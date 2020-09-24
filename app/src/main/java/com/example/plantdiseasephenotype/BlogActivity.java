package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        recyclerView = findViewById(R.id.blog_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_blogs);
        navbar.setOnNavigationItemSelectedListener(this);

        getBlogList();
    }

    public void getBlogList() {
        Call<BlogsList> blogsAPICall = BloggerAPI.getBlogService().getBlogList();
        blogsAPICall.enqueue(new Callback<BlogsList>() {
            @Override
            public void onResponse(Call<BlogsList> call, Response<BlogsList> response) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                BlogsList blogsList = response.body();
                recyclerView.setAdapter(new BlogAdapter(BlogActivity.this, blogsList.getItems()));
            }

            @Override
            public void onFailure(Call<BlogsList> call, Throwable t) {
                Toast.makeText(BlogActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BlogActivity.this, HomeActivity.class));
            }
        });
    }

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
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
        }
        return false;
    }
}
