package com.masterprojekat.music_online_classes.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Comment;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorsCourseAdapter extends RecyclerView.Adapter<ProfessorsCourseAdapter.ProfessorsCourseViewHolder> {
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final Context context;
    private final List<Course> coursesList;

    public ProfessorsCourseAdapter(Context context, List<Course> coursesList) {
        this.context = context;
        this.coursesList = (coursesList != null) ? coursesList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProfessorsCourseAdapter.ProfessorsCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.professors_course_item, parent, false);
        return new ProfessorsCourseAdapter.ProfessorsCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorsCourseViewHolder holder, int position) {
        Course course = coursesList.get(position);
        holder.courseName.setText(course.getName());

        displayImage(holder, course.getCourseImage());

        holder.editCourse.setOnClickListener(view -> {
            editCoursesInfo(course);
        });
    }

    private void editCoursesInfo(Course courseToEdit) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.edit_course_information_dialog, null);

        // Set previous fields

    }

    private void displayImage(ProfessorsCourseAdapter.ProfessorsCourseViewHolder holder, String fullImagePath) {
        String imageFile = fullImagePath.substring(fullImagePath.lastIndexOf("/") + 1);
        courseApi.getCourseImage(imageFile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    Glide.with(context)
                            .load(bitmap)
                            .into(holder.courseImage);
                } else {
                    holder.courseImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.broken_image));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(ProfessorsCourseAdapter.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem slike kursa nije uspeo!", throwable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public static class ProfessorsCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseName;
        ImageButton editCourse;

        public ProfessorsCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.course_image);
            courseName = itemView.findViewById(R.id.course_name);
            editCourse = itemView.findViewById(R.id.edit_courses_button);
        }
    }
}
