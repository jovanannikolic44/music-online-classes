package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CommentAPI {

    @POST("/comments/save-comment")
    Call<Void> saveComment(@Body Comment comment);
    @GET("/comments/get-all-comments-for-course")
    Call<List<Comment>> getAllCommentsForCourse(@Query("courseId") int courseId);
}
