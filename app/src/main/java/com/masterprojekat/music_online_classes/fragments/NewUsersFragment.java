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

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.NewUsersAdapter;
import com.masterprojekat.music_online_classes.helpers.ReservationsAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUsersFragment extends Fragment {
    private static final String TAG = "NewUsersFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
    private User loggedInUser;
    private final List<User> userRequestsList = new ArrayList<>();
    private NewUsersAdapter newUsersAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;

            setUpNewUsersAdapter(view);
            displayUserRequests();
        });
    }

    private void displayUserRequests() {
        userAPI.getUserAccountRequests().enqueue(new Callback<List<User>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<User> userRequestsResponseList = response.body();
                    userRequestsList.clear();
                    userRequestsList.addAll(userRequestsResponseList);
                    newUsersAdapter.notifyDataSetChanged();

                    if(userRequestsResponseList.isEmpty()) {
                        Toast.makeText(getContext(), "Ne postoji ni jedan zahtev!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.w(TAG, "Dohvatanje zahteva za novim nalozima nije uspesan: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem zahteva za novim nalozima nije uspeo!", throwable);
            }
        });
    }

    private void setUpNewUsersAdapter(View view) {
        RecyclerView newUsersRecyclerView = view.findViewById(R.id.new_user_requests_recycler_view);
        newUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newUsersAdapter = new NewUsersAdapter(userRequestsList);
        newUsersRecyclerView.setAdapter(newUsersAdapter);
        newUsersRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_users, container, false);
    }
}
