package com.masterprojekat.music_online_classes.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageButton;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.Cart;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.ProgressAdater;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);
    private User loggedInUser;
    private final List<Course> purchasedCoursesList = new ArrayList<>();
    private ProgressAdater progressAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;
            setUpProgressAdapter(view);
            getPurchasedCourses();

            ImageButton currentCoursesCartButton = view.findViewById(R.id.current_courses_cart);
            currentCoursesCartButton.setOnClickListener(localView -> {
                Intent cartIntent = new Intent(getActivity(), Cart.class);
                cartIntent.putExtra("loggedInUser", loggedInUser);
                startActivity(cartIntent);
            });
        });
    }

    private void getPurchasedCourses() {
        userApi.getPurchasedCourses(loggedInUser.getUsername()).enqueue(new Callback<List<Course>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Course> coursesResponseList = response.body();
                    purchasedCoursesList.clear();
                    purchasedCoursesList.addAll(coursesResponseList);
                    progressAdapter.notifyDataSetChanged();
                }
                else {
                    Log.w(TAG, "Dohvatanje kupljenih kurseva nije uspesno: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Zahtev za dohvatanjem kupljenih kurseva nije uspeo!", throwable);
            }
        });
    }

    private void setUpProgressAdapter(View view) {
        RecyclerView progressBarRecyclerView = view.findViewById(R.id.statistics_recycler_view);
        progressBarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressAdapter = new ProgressAdater(purchasedCoursesList);
        progressBarRecyclerView.setAdapter(progressAdapter);
        progressBarRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }
}
