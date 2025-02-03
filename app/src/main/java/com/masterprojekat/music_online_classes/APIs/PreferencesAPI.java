package com.masterprojekat.music_online_classes.APIs;

import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PreferencesAPI {

    @POST("/preferences/save-preferences")
    Call<ResponseBody> savePreferences(@Query("username") String username, @Body Set<String> instruments);

    @GET("/preferences/get-preferences")
    Call<ResponseBody> getPreferences(@Query("username") String username);
}
