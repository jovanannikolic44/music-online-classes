package com.masterprojekat.music_online_classes.APIs;

import com.masterprojekat.music_online_classes.models.Term;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TermAPI {
    @POST("/term/create-new")
    Call<String> createNewTerm(@Body Term term);

    @POST("/term/reserve")
    Call<ResponseBody> requestTerm(@Query("termId") int termId, @Query("studentUsername") String studentUsername, @Query("courseId") int courseId);

    @POST("/term/accept")
    Call<ResponseBody> acceptTerm(@Query("termId") int termId);

    @POST("/term/decline")
    Call<ResponseBody> rejectTerm(@Query("termId") int termId);

    @GET("/term/get-available-terms-for-professor")
    Call<List<Term>> getAllAvailableTermsForProfessor(@Query("professorUsername") String professorUsername);

    @GET("/term/get-requested-terms-for-professor")
    Call<List<Term>> getAllRequestedTermsForProfessor(@Query("professorUsername") String professorUsername);

    @GET("/term/get-confirmed-terms-for-professor")
    Call<List<Term>> getAllConfirmedTermsForProfessor(@Query("professorUsername") String professorUsername);

    @GET("/term/get-confirmed-terms-for-student")
    Call<List<Term>> getAllConfirmedTermsForStudent(@Query("studentUsername") String studentUsername);

    @GET("/term/get-terms-by-date")
    Call<List<Term>> getTermsByDate(@Query("studentUsername") String studentUsername, @Query("inputDate") String inputDate);
}
