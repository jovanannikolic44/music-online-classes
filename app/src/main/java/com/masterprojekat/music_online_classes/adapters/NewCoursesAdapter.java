package com.masterprojekat.music_online_classes.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.api.CourseAPI;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCoursesAdapter extends RecyclerView.Adapter<NewCoursesAdapter.NewCoursesViewHolder>{
    private static final String TAG = "NewCoursesAdapter";
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseAPI = retrofitService.getRetrofit().create(CourseAPI.class);
    private final List<Course> courseRequestsList;

    public NewCoursesAdapter(List<Course> courseRequestsList) {
        this.courseRequestsList = courseRequestsList;
    }
    @NonNull
    @Override
    public NewCoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_course_item, parent, false);
        return new NewCoursesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewCoursesAdapter.NewCoursesViewHolder holder, int position) {
        Course course = courseRequestsList.get(position);
        holder.newCourseName.setText(course.getName());
        holder.newCourseProfessor.setText(course.getProfessor().getName() + " " + course.getProfessor().getSurname());
        holder.newCoursePrice.setText(String.valueOf(course.getPrice()));
        holder.newCourseInstrument.setText(course.getInstrument());
        holder.newCourseLevel.setText(course.getLevel());

        holder.acceptButton.setOnClickListener(view -> {
            acceptRequest(holder, course);
        });

        holder.declineButton.setOnClickListener(view -> {
            declineRequest(holder, course);
        });
    }

    private void acceptRequest(@NonNull NewCoursesAdapter.NewCoursesViewHolder holder, Course course) {
        courseAPI.acceptRequest(course.getCourseId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeCourseRequestFromList(holder.getBindingAdapterPosition());
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri odobravanju novog kursa! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri odobravanju novog kursa! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void declineRequest(@NonNull NewCoursesAdapter.NewCoursesViewHolder holder, Course course) {
        courseAPI.declineRequest(course.getCourseId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeCourseRequestFromList(holder.getBindingAdapterPosition());
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri odbijanju novog kursa! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri odbijanju novog kursa! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void removeCourseRequestFromList(int position) {
        courseRequestsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return courseRequestsList.size();
    }

    public static class NewCoursesViewHolder extends RecyclerView.ViewHolder {
        TextView newCourseName, newCourseProfessor, newCoursePrice, newCourseInstrument, newCourseLevel;
        Button acceptButton, declineButton;

        public NewCoursesViewHolder(@NonNull View itemView) {
            super(itemView);
            newCourseName = itemView.findViewById(R.id.new_course_name);
            newCourseProfessor = itemView.findViewById(R.id.new_course_professor);
            newCoursePrice = itemView.findViewById(R.id.new_course_price);
            newCourseInstrument = itemView.findViewById(R.id.new_course_instrument);
            newCourseLevel = itemView.findViewById(R.id.new_course_level);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
