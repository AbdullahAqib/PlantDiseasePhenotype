package com.example.plantdiseasephenotype.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.plantdiseasephenotype.models.NewsResponse;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsAPI {
    public static final String BASE_URL = "https://newsapi.org/v2/";
    private static NewsService newsService = null;

    public static NewsService getNewsService(final Context context){

        OkHttpClient client = new OkHttpClient
                .Builder()
                .cache(new Cache(context.getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .addInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (isNetworkAvailable(context)) {
                            request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                        } else {
                            request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                        }
                        return chain.proceed(request);
                    }
                })
                .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            newsService = retrofit.create(NewsService.class);

        return newsService;
    }

    public interface NewsService {
        @GET("everything")
        Call<NewsResponse> getLatestNews(@Query("q") String q, @Query("apiKey") String apiKey);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
