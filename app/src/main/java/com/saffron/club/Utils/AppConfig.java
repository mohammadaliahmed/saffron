package com.saffron.club.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
    public static String BASE_URL = "https://saffronclub.com.au/";
    public static String BASE_URL_Image = "http://saffronclub.com.au/web/core/storage/app/";
    public static String TOKKEN = "http://acnure.com/";

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }  public static Retrofit getTokenUrl() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.TOKKEN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
