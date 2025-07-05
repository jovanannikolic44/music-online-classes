package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.CourseProgress;
import com.masterprojekat.music_online_classes.models.Term;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseProgressAPI {
    @PUT("/courses/markClassHeld")
    Call<CourseProgress> markClassHeld(@Query("termId") int termId);

    @PUT("/courses/markClassNotHeld")
    Call<Term> markClassNotHeld(@Query("termId") int termId);
    @GET("/courses/{courseId}/progress/{username}")
    Call<CourseProgress> getCourseProgress(@Path("courseId") int courseId, @Path("username") String username);
}
