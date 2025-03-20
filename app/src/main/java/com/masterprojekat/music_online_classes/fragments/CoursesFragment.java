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
import com.masterprojekat.music_online_classes.APIs.PreferencesAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.CourseAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final PreferencesAPI preferencesApi = retrofitService.getRetrofit().create(PreferencesAPI.class);

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            if (loggedInUser == null)
                return;

            EditText searchCoursesEditText = view.findViewById(R.id.search_courses);
            searchCoursesEditText.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String inputSearch = String.valueOf(searchCoursesEditText.getText()).trim();
                    if(!inputSearch.isEmpty()) {
                        displaySearchedCourses(view, inputSearch);
                    }
                    return true;
                }
                return false;
            });

            // prikaz preporucenih kurseva
            displayRecommendedCourses(view, loggedInUser);

            // prikaz najbolje ocenjenih kurseva
            // prikaz najpristupacnijih kurseva
            // poslednji put pristupljeni kursevi (do 10 maks)
        });
    }

    private void displayRecommendedCourses(View view, User loggedInUser) {
        List<Course> recommendedCoursesList = new ArrayList<>();
        RecyclerView recommendedCourses = view.findViewById(R.id.recommended_courses);
        recommendedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter courseAdapter = new CourseAdapter(getContext(), recommendedCoursesList);
        recommendedCourses.setAdapter(courseAdapter);
        recommendedCourses.setVisibility(View.VISIBLE);

        // Recommended kursevi se prikazuju na osnovu preferncija
        // dohvatiti preferencije ulogovanog user-a
        getUserPreferences(loggedInUser);

    }

    private void getUserPreferences(User loggedInUser) {
        preferencesApi.getPreferences(loggedInUser.getUsername()).enqueue(new Callback<Set<String>>() {

            @Override
            public void onResponse(@NonNull Call<Set<String>> call, @NonNull Response<Set<String>> response) {
                Set<String> preferences = response.body();
                System.out.println("Preferences");
                for(String pref : preferences) {
                    System.out.println(pref);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Set<String>> call, @NonNull Throwable throwable) {
                System.out.println("REQ error");
            }
        });
    }

    private void displaySearchedCourses(View view, String inputSearch) {
        List<Course> searchedCoursesList = new ArrayList<>();
        RecyclerView searchedCourses = view.findViewById(R.id.searched_courses);
        searchedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter searchedCourseAdapter = new CourseAdapter(getContext(), searchedCoursesList);
        searchedCourses.setAdapter(searchedCourseAdapter);

        searchedCourses.setVisibility(View.VISIBLE);
        search(searchedCourseAdapter, searchedCoursesList, inputSearch);

        // !!! Ostale staviti na visibility GONE
    }

    private void search(CourseAdapter searchedCourseAdapter, List<Course> searchedCoursesList, String inputSearch) {
        courseApi.searchCourses(inputSearch).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                List<Course> searchedCourses = response.body();
                searchedCoursesList.clear();
                if(searchedCourses == null) {
                    Toast.makeText(requireContext(), "Ne postoji ni jedan kurs za prikaz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                searchedCoursesList.addAll(searchedCourses);
                searchedCourseAdapter.notifyDataSetChanged();
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
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }
}
