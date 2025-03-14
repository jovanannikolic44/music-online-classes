package com.masterprojekat.music_online_classes.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_scroll, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.professorName.setText("Profesor: " + course.getProfessor().getName());
        holder.courseRating.setText("⭐ " + course.getRating());
        holder.coursePrice.setText("RSD" + course.getPrice());

        Glide.with(context).load(course.getCourseImage()).into(holder.courseImage);
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
