package com.masterprojekat.music_online_classes.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.CourseAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);

    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            if (loggedInUser == null)
                return;

            System.out.println("Logged in user " + loggedInUser.getUsername());
            EditText searchCoursesEditText = view.findViewById(R.id.search_courses);
            searchCoursesEditText.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String inputSearch = String.valueOf(searchCoursesEditText.getText()).trim();
                    if(!inputSearch.isEmpty()) {
                        search(inputSearch);
                    }
                    return true;
                }
                return false;
            });
        });

        coursesRecyclerView = view.findViewById(R.id.searched_courses);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        courseAdapter = new CourseAdapter(getContext(), courseList);
        coursesRecyclerView.setAdapter(courseAdapter);
    }

    private void search(String inputSearch) {
        System.out.println("Calling search with " + inputSearch);
        courseApi.searchCourses(inputSearch).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                List<Course> searchedCourses = response.body();
                courseList.clear();
                if(searchedCourses == null) {
                    Toast.makeText(requireContext(), "Ne postoji ni jedan kurs za prikaz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                courseList.addAll(searchedCourses);
                courseAdapter.notifyItemRangeInserted(0, searchedCourses.size());
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Pretraga kurseva ne funkcionise!", throwable);
            }
        });
    }

    private List<Course> getAllCourses() {
        courseApi.getAllCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                System.out.println("List of courses is returned");
                List<Course> allCourses = response.body();
                for(int i = 0;  i < allCourses.size(); i++) {
                    System.out.println(allCourses.get(i).getName() );
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Dohvatanje svih kurseva ne funkcionise!", throwable);
            }
        });
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }
}