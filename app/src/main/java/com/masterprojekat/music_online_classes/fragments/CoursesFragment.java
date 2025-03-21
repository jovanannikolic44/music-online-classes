package com.masterprojekat.music_online_classes.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.PreferencesAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.CourseAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final PreferencesAPI preferencesApi = retrofitService.getRetrofit().create(PreferencesAPI.class);
    private User loggedInUser;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;

            loggedInUser = user;
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

            searchCoursesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty()) {
                        resetSearch(view);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            displayRecommendedCourses(view);
            displayBestRatedCourses(view);
            displayCheapestCourses(view);
        });
    }

    private void displayCheapestCourses(View view) {
        List<Course> cheapestCoursesList = new ArrayList<>();
        RecyclerView recommendedCourses = view.findViewById(R.id.cheapest_courses);
        recommendedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter courseAdapter = new CourseAdapter(getContext(), cheapestCoursesList, loggedInUser);
        recommendedCourses.setAdapter(courseAdapter);
        recommendedCourses.setVisibility(View.VISIBLE);

        getCheapestCourses(courseAdapter, cheapestCoursesList);
    }

    private void getCheapestCourses(CourseAdapter courseAdapter, List<Course> cheapestCoursesList) {
        courseApi.getCheapestCourses().enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                List<Course> cheapestCourses = response.body();
                if(cheapestCourses == null) {
                    Toast.makeText(requireContext(), "Nije nadjen ni jedan kurs!", Toast.LENGTH_SHORT).show();
                    return;
                }
                cheapestCoursesList.clear();
                cheapestCoursesList.addAll(cheapestCourses);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Dohvatanje najjeftinijih kurseva ne funkcionise!", throwable);
            }
        });
    }

    private void displayBestRatedCourses(View view) {
        List<Course> bestRatedCoursesList = new ArrayList<>();
        RecyclerView recommendedCourses = view.findViewById(R.id.best_rated_courses);
        recommendedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter courseAdapter = new CourseAdapter(getContext(), bestRatedCoursesList, loggedInUser);
        recommendedCourses.setAdapter(courseAdapter);
        recommendedCourses.setVisibility(View.VISIBLE);

        getBestRatedCourses(courseAdapter, bestRatedCoursesList);
    }

    private void getBestRatedCourses(CourseAdapter courseAdapter, List<Course> bestRatedCoursesList) {
        courseApi.getBestRatedCourses().enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                List<Course> bestCourses = response.body();
                if(bestCourses == null) {
                    Toast.makeText(requireContext(), "Nije nadjen ni jedan kurs!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bestRatedCoursesList.clear();
                bestRatedCoursesList.addAll(bestCourses);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Dohvatanje najboljih kurseva ne funkcionise!", throwable);
            }
        });
    }

    private void displayRecommendedCourses(View view) {
        List<Course> recommendedCoursesList = new ArrayList<>();
        RecyclerView recommendedCourses = view.findViewById(R.id.recommended_courses);
        recommendedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter courseAdapter = new CourseAdapter(getContext(), recommendedCoursesList, loggedInUser);
        recommendedCourses.setAdapter(courseAdapter);
        recommendedCourses.setVisibility(View.VISIBLE);

        getUserPreferences(courseAdapter, recommendedCoursesList);
    }

    private void getUserPreferences(CourseAdapter courseAdapter, List<Course> recommendedCoursesList) {
        preferencesApi.getPreferences(loggedInUser.getUsername()).enqueue(new Callback<Set<String>>() {

            @Override
            public void onResponse(@NonNull Call<Set<String>> call, @NonNull Response<Set<String>> response) {
                Set<String> preferences = response.body();
                if(preferences == null) {
                    Toast.makeText(requireContext(), "Korisnik nema preporucen kurs!", Toast.LENGTH_SHORT).show();
                    return;
                }

                courseApi.getCoursesByPreference(preferences).enqueue(new Callback<List<Course>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                        List<Course> courses = response.body();
                        if (courses != null) {
                            recommendedCoursesList.clear();
                            recommendedCoursesList.addAll(courses);
                            courseAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                        Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Dohvatanje kurseva na osnovu preferencija ne funkcionise!", throwable);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Set<String>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Dohvatanje preferencija ne funkcionise!", throwable);
            }
        });
    }

    private void displaySearchedCourses(View view, String inputSearch) {
        List<Course> searchedCoursesList = new ArrayList<>();
        RecyclerView searchedCourses = view.findViewById(R.id.searched_courses);
        searchedCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        CourseAdapter searchedCourseAdapter = new CourseAdapter(getContext(), searchedCoursesList, loggedInUser);
        searchedCourses.setAdapter(searchedCourseAdapter);

        searchedCourses.setVisibility(View.VISIBLE);
        search(searchedCourseAdapter, searchedCoursesList, inputSearch);

        RecyclerView recommendedCourses = view.findViewById(R.id.recommended_courses);
        RecyclerView bestRatedCourses = view.findViewById(R.id.best_rated_courses);
        RecyclerView cheapestCourses = view.findViewById(R.id.cheapest_courses);
        TextView searchedCoursesLabel = view.findViewById(R.id.searched_courses_label);
        TextView recommendedCoursesLabel = view.findViewById(R.id.recommended_courses_label);
        TextView bestRatedCoursesLabel = view.findViewById(R.id.best_rated_courses_label);
        TextView cheapestCoursesLabel = view.findViewById(R.id.cheapest_courses_label);
        searchedCoursesLabel.setVisibility(View.VISIBLE);
        recommendedCourses.setVisibility(View.GONE);
        recommendedCoursesLabel.setVisibility(View.GONE);
        bestRatedCourses.setVisibility(View.GONE);
        bestRatedCoursesLabel.setVisibility(View.GONE);
        cheapestCourses.setVisibility(View.GONE);
        cheapestCoursesLabel.setVisibility(View.GONE);
    }

    private void search(CourseAdapter searchedCourseAdapter, List<Course> searchedCoursesList, String inputSearch) {
        courseApi.searchCourses(inputSearch).enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                List<Course> searchedCourses = response.body();
                if(searchedCourses == null) {
                    Toast.makeText(requireContext(), "Ne postoji ni jedan kurs za prikaz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                searchedCoursesList.clear();
                searchedCoursesList.addAll(searchedCourses);
                searchedCourseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(CoursesFragment.class.getName()).log(Level.SEVERE, "Greška! Pretraga kurseva ne funkcionise!", throwable);
            }
        });
    }

    private void resetSearch(View view) {
        RecyclerView searchedCourses = view.findViewById(R.id.searched_courses);
        RecyclerView recommendedCourses = view.findViewById(R.id.recommended_courses);
        RecyclerView bestRatedCourses = view.findViewById(R.id.best_rated_courses);
        RecyclerView cheapestCourses = view.findViewById(R.id.cheapest_courses);
        TextView searchedCoursesLabel = view.findViewById(R.id.searched_courses_label);
        TextView recommendedCoursesLabel = view.findViewById(R.id.recommended_courses_label);
        TextView bestRatedCoursesLabel = view.findViewById(R.id.best_rated_courses_label);
        TextView cheapestCoursesLabel = view.findViewById(R.id.cheapest_courses_label);

        searchedCourses.setVisibility(View.GONE);
        searchedCoursesLabel.setVisibility(View.GONE);
        recommendedCourses.setVisibility(View.VISIBLE);
        recommendedCoursesLabel.setVisibility(View.VISIBLE);
        bestRatedCourses.setVisibility(View.VISIBLE);
        bestRatedCoursesLabel.setVisibility(View.VISIBLE);
        cheapestCourses.setVisibility(View.VISIBLE);
        cheapestCoursesLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }
}
