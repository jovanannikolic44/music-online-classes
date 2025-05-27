package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseAPI {
    @GET("/course/get-all")
    Call<List<Course>> getAllCourses();

    @GET("course/search")
    Call<List<Course>> searchCourses(@Query("searchText") String searchText);

    @GET("/course/get-image")
    Call<ResponseBody> getCourseImage(@Query("imageName") String imageName);

    @POST("/course/by-preference")
    Call<List<Course>> getCoursesByPreference(@Body Set<String> preferences);

    @GET("course/best-rated")
    Call<List<Course>> getBestRatedCourses();

    @GET("/course/cheapest")
    Call<List<Course>> getCheapestCourses();

    @POST("/course/save-rating")
    Call<Void> saveCourseRating(@Query("courseId") int courseId, @Query("rating") float rating);

    @GET("/course/get-rating")
    Call<Float> getCourseRating(@Query("courseId") int courseId);

    @GET("/course/professors/{professorUsername}")
    Call<List<Course>> getAllCoursesByProfessor(@Path("professorUsername") String professorUsername);

    @Multipart
    @POST("/course/add-new")
    Call<Void> addCourse(@Part("name") RequestBody name, @Part("price") RequestBody price, @Part("professorUsername") RequestBody professorUsername,
                         @Part("level") RequestBody level, @Part("instrument") RequestBody instrument, @Part("description") RequestBody description,
                         @Part("content") RequestBody content, @Part("numberOfClasses") RequestBody numberOfClasses, @Part MultipartBody.Part image);

    @PUT("/course/update-info")
    Call<Course> updateCourseInfo(@Body Course course);
}
