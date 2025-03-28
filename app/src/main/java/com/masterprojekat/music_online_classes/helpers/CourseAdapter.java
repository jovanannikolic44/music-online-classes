package com.masterprojekat.music_online_classes.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.CourseDetails;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final Context context;
    private final List<Course> courseList;
    private final User loggedInUser;

    public CourseAdapter(Context context, List<Course> courseList, User loggedInUser) {
        this.context = context;
        this.courseList = courseList;
        this.loggedInUser = loggedInUser;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_scroll, parent, false);
        return new CourseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.professorName.setText("Profesor: " + course.getProfessor().getName() + " " + course.getProfessor().getSurname());
        holder.courseRating.setText("⭐ " + course.getRating());
        holder.coursePrice.setText("RSD" + course.getPrice());

        displayImage(holder, course.getCourseImage());

        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(1.5f)
                    .scaleY(1.5f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();
                    })
                    .start();
            Intent intent = new Intent(v.getContext(), CourseDetails.class);
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("course", course);
            v.getContext().startActivity(intent);
        });
    }

    public void displayImage(CourseViewHolder holder, String fullImagePath) {
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
                System.out.println("Error");
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseName, professorName, courseRating, coursePrice;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseName = itemView.findViewById(R.id.courseName);
            professorName = itemView.findViewById(R.id.professorName);
            courseRating = itemView.findViewById(R.id.courseRating);
            coursePrice = itemView.findViewById(R.id.coursePrice);
        }
    }
}
