package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CourseAPI {
    @GET("/course/get-all")
    Call<List<Course>> getAllCourses();

    @GET("course/search")
    Call<List<Course>> searchCourses(@Query("searchText") String searchText);

    @GET("/course/get-image")
    Call<ResponseBody> getCourseImage(@Query("imageName") String imageName);
}
