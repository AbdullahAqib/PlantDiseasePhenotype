package com.example.plantdiseasephenotype;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class BloggerAPI {
    public static final String key = "AIzaSyC2f9qoCTMHpU_dLDE0hY82FFMDzAVq9N0";
    public static final String url = "https://www.googleapis.com/blogger/v3/blogs/8606144701991845994/posts/";

    public static BlogService blogService = null;

    public static BlogService getBlogService(){

        if(blogService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            blogService = retrofit.create(BlogService.class);
        }

        return blogService;
    }

    public interface BlogService {
        @GET("?key="+key)
        Call<BlogsList> getBlogList();
    }
}
