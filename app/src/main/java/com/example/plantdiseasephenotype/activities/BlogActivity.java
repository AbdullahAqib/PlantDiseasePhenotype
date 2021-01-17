package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.plantdiseasephenotype.models.NewsArticle;
import com.example.plantdiseasephenotype.models.NewsResponse;
import com.example.plantdiseasephenotype.network.BloggerAPI;
import com.example.plantdiseasephenotype.models.BlogsList;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.adapters.BlogAdapter;
import com.example.plantdiseasephenotype.network.NewsAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private BlogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        recyclerView = findViewById(R.id.blog_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_blogs);
        navbar.setOnNavigationItemSelectedListener(this);

        int id = getIntent().getIntExtra("id", 0);

        if(id==R.id.btn_news){
            getNewsList();
        }else{
            getBlogList();
        }
    }

    public void getBlogList() {
        Call<BlogsList> blogsAPICall = BloggerAPI.getBlogService(getApplicationContext()).getBlogList();
        blogsAPICall.enqueue(new Callback<BlogsList>() {
            @Override
            public void onResponse(Call<BlogsList> call, Response<BlogsList> response) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                BlogsList blogsList = response.body();
                adapter = new BlogAdapter(BlogActivity.this, blogsList.getItems());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BlogsList> call, Throwable t) {
                Toast.makeText(BlogActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BlogActivity.this, HomeActivity.class));
            }
        });
    }

    public void getNewsList(){
        Call<NewsResponse> call = NewsAPI.getNewsService(getApplicationContext()).getLatestNews("agriculture","889f1962d48143bf974b539215eb9e99");
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse>call, Response<NewsResponse> response) {
                if(response.body().getStatus().equals("ok")) {
                    findViewById(R.id.progressbar).setVisibility(View.GONE);
                    List<NewsArticle> articleList = response.body().getArticles();
                    if(articleList.size()>0) {
                        adapter = new BlogAdapter(BlogActivity.this, articleList, true);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<NewsResponse>call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                startActivity(new Intent(getApplicationContext(), CameraXActivity.class));
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
