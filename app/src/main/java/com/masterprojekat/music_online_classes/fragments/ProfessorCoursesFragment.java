package com.masterprojekat.music_online_classes.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.ProfessorsCourseAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.helpers.TermAdapter;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorCoursesFragment extends Fragment {
    private static final String TAG = "ProfessorsCoursesFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseAPI = retrofitService.getRetrofit().create(CourseAPI.class);
    private User loggedInUser;
    private final List<Course> allProfessorsCourses = new ArrayList<>();
    private ProfessorsCourseAdapter professorsCourseAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;
            setUpProfessorsCoursesAdapter(view);
            displayProfessorsCourses();
        });
    }

    private void displayProfessorsCourses() {
        courseAPI.getAllCoursesByProfessor(loggedInUser.getUsername()).enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Course> coursesResponseList = response.body();
                    allProfessorsCourses.clear();
                    allProfessorsCourses.addAll(coursesResponseList);
                    professorsCourseAdapter.notifyDataSetChanged();
                }
                else {
                    Log.w(TAG, "Dohvatanje kurseva nije uspesno: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem kurseva nije uspeo!", throwable);
            }
        });
    }

    private void setUpProfessorsCoursesAdapter(View view) {
        RecyclerView professorsCoursesRecyclerView = view.findViewById(R.id.professors_courses_recycler_view);
        professorsCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        professorsCourseAdapter = new ProfessorsCourseAdapter(requireContext(), allProfessorsCourses);
        professorsCoursesRecyclerView.setAdapter(professorsCourseAdapter);
        professorsCoursesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_professor_courses, container, false);
    }
}
