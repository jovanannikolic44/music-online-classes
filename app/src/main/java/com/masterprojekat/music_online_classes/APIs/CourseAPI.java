package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
}
