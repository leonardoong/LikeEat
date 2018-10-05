package com.example.android.likeeatapplication.API;

import com.example.android.likeeatapplication.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET ("top-headlines")
    Call<News> getNews(

            @Query("country") String country,
            @Query("apiKey") String apikey
            ,@Query("category") String category
    );

}
