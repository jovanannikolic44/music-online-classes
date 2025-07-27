package com.masterprojekat.music_online_classes.api;

import com.masterprojekat.music_online_classes.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationAPI {
    @GET("/notification/get-all-for-student")
    Call<List<Notification>> getAllNotificationsForStudent(@Query("student_username") String student_username);

    @POST("/notification/create-new-notification")
    Call<Void> createNewNotification(@Query("termId") int termId, @Query("acceptOrRejectMessage") String acceptOrRejectMessage);

    @GET("/notification/search")
    Call<List<Notification>> searchNotifications(@Query("username") String username, @Query("inputSearch") String inputSearch);
}
