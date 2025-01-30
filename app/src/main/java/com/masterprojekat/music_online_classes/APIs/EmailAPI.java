package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.User;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EmailAPI {

    @POST("/email/send")
    Call<Void> sendEmail(@Query("toEmail") String toEmail, @Query("subject") String subject, @Query("body") String body);
}
