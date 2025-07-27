package com.masterprojekat.music_online_classes.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.masterprojekat.music_online_classes.api.CourseAPI;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.adapters.helpers.NewCoursesAdapter;
import com.masterprojekat.music_online_classes.utils.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCoursesFragment extends Fragment {
    private static final String TAG = "NewCoursesFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseAPI = retrofitService.getRetrofit().create(CourseAPI.class);
    private User loggedInUser;
    private final List<Course> courseRequestsList = new ArrayList<>();
    private NewCoursesAdapter newCoursesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;

            setUpNewCoursesAdapter(view);
            displayCourseRequests();
        });
    }

    private void displayCourseRequests() {
        courseAPI.getNewCourseRequests().enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Course> courseRequestsResponseList = response.body();
                    courseRequestsList.clear();
                    courseRequestsList.addAll(courseRequestsResponseList);
                    newCoursesAdapter.notifyDataSetChanged();

                    if(courseRequestsResponseList.isEmpty()) {
                        Toast.makeText(getContext(), "Ne postoji ni jedan zahtev!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.w(TAG, "Dohvatanje zahteva za novim kurseima nije uspesan: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanje novih kurseva nije uspeo!", throwable);
            }
        });
    }

    private void setUpNewCoursesAdapter(View view) {
        RecyclerView newCoursesRecyclerView = view.findViewById(R.id.new_courses_recycler_view);
        newCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newCoursesAdapter = new NewCoursesAdapter(courseRequestsList);
        newCoursesRecyclerView.setAdapter(newCoursesAdapter);
        newCoursesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_courses, container, false);
    }
}