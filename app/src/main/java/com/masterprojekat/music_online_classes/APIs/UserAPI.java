package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.User;

import java.util.List;
import java.util.Map;

import kotlin.ParameterName;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserAPI {

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @GET("/user/get-by-username")
    Call<User> getUserByUsername(@Query("username") String username);

    @GET("/user/check-email-and-phone-number")
    Call<ResponseBody> checkEmailAndPhoneNumberUniqueness(@Query("email") String email, @Query("phoneNumber") String phoneNumber);

    @POST("/user/save")
    Call<User> saveUser(@Body User user);

    @POST("/user/update-info")
    Call<User> updateUserInfo(@Body User user);

    @POST("/user/update-password")
   Call<User> updateUserPassword(@Query("username") String username, @Query("newPassword") String newPassword);

    @Multipart
    @POST("/user/upload-profile-picture")
    Call<ResponseBody> uploadProfilePicture(@Part MultipartBody.Part file, @Query("username") String username);

    @GET("/user/get-profile-picture")
    Call<ResponseBody> getProfilePicture(@Query("username") String username);

}
