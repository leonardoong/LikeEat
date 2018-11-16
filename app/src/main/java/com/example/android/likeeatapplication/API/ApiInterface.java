package com.example.android.likeeatapplication.API;

import com.example.android.likeeatapplication.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET ("top-headlines")
    Call<News> getNews(

            @Query("country") String country,
            @Query("apiKey") String apikey,
//            @Query("source") String source
            @Query("category") String category
    );

    @GET("everything")
    Call<News> getNewsSearch(
            @Query("q") String keyword,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
//            @Query("source") String source
            @Query("apiKey") String apikey
    );

}
