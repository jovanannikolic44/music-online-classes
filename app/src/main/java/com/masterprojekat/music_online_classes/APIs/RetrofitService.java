package com.masterprojekat.music_online_classes.APIs;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public RetrofitService() {
        initalizeRetrofit();
    }

    private void initalizeRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.24:9000")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
