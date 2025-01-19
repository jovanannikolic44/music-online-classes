package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserAPI {

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @POST("/user/save")
    Call<User> saveUser(@Body User user);
}
