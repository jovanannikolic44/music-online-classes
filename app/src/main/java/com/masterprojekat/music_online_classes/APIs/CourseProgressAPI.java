package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.CourseProgress;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseProgressAPI {
    @PUT("/courses/{course_id}/markClassHeld")
    Call<CourseProgress> markClassHeld(@Path("courseId") int courseId, @Query("username") String username);

    @GET("/courses/{courseId}/progress/{username}")
    Call<CourseProgress> getCourseProgress(@Path("courseId") int courseId, @Path("username") String username);
}
