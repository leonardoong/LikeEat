package com.example.android.likeeatapplication.Retrofit;

import com.example.android.likeeatapplication.Model.WeatherResult;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Leonardo on 9/18/2018.
 */

public interface IOpenWeatherMap{
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                 @Query("lon") String lon,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);
}
