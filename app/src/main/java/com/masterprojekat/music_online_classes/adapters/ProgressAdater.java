package com.masterprojekat.music_online_classes.adapters;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;

public class ProgressAdater extends RecyclerView.Adapter<ProgressAdater.ProgressViewHolder> {
    private final List<Course> courseList;

    public ProgressAdater(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
        return new ProgressViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.professorName.setText("Profesor: " + course.getProfessor().getName());
        holder.courseLevel.setText("Nivo: " + course.getLevel());
        holder.progressText.setText("Napredak: " + course.getProgress() + "%");
        holder.progressBar.setProgress(course.getProgress());

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if(position == courseList.size() - 1) {
            layoutParams.bottomMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 150,
                    holder.itemView.getResources().getDisplayMetrics());
        }
        else {
            layoutParams.bottomMargin = 0;
        }
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, professorName, courseLevel, progressText;
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            professorName = itemView.findViewById(R.id.professor);
            courseLevel = itemView.findViewById(R.id.course_level);
            progressText = itemView.findViewById(R.id.progress_text);
            progressBar = itemView.findViewById(R.id.course_progress_bar);
        }
    }
}
