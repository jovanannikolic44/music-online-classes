package com.masterprojekat.music_online_classes.APIs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PasswordResetAPI {

    @POST("/reset-service/request-reset")
    Call<Void> requestPasswordReset(@Query("toEmail") String toEmail);

    @POST("/reset-service/update-password")
    Call<ResponseBody> updatePassword(@Query("token") String token, @Query("newPassword") String newPassword);
}
