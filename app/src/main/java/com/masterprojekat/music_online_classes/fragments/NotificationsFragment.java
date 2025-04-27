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
import android.widget.Toast;

import com.masterprojekat.music_online_classes.APIs.NotificationAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.ReserveTerm;
import com.masterprojekat.music_online_classes.helpers.NotificationAdapter;
import com.masterprojekat.music_online_classes.helpers.ProgressAdater;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.Notification;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationsFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final NotificationAPI notificationApi = retrofitService.getRetrofit().create(NotificationAPI.class);
    private User loggedInUser;
    private List<Notification> notificationsList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;
            setUpAdapter(view);
            getAllNotifications();

            EditText searchNotificationEditText = view.findViewById(R.id.notification_search);
            searchNotificationEditText.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String inputSearch = String.valueOf(searchNotificationEditText.getText()).trim();
                    if (!inputSearch.isEmpty()) {
                        searchNotifications(inputSearch);
                    } else {
                        getAllNotifications();
                    }
                    return true;
                }
                return false;
            });
            searchNotificationEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty()) {
                        getAllNotifications();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        });
    }
    private void setUpAdapter(View view) {
        RecyclerView notificationRecycleView = view.findViewById(R.id.notification_recycler_view);
        notificationRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(getContext(), notificationsList);
        notificationRecycleView.setAdapter(notificationAdapter);
        notificationRecycleView.setVisibility(View.VISIBLE);
    }

    public void searchNotifications(String inputSearch) {
        notificationApi.searchNotifications(loggedInUser.getUsername(), inputSearch).enqueue(new Callback<List<Notification>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Notification> allNotifications = response.body();
                    notificationsList.clear();
                    notificationsList.addAll(allNotifications);
                    notificationAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(requireContext(), "Greska pri pretrazi notifikacija!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable throwable) {
                Logger.getLogger(NotificationsFragment.class.getName()).log(Level.SEVERE, "Greska! Zahtev za pretragom notifikacija nije uspeo!", throwable);
            }
        });
    }

    private void getAllNotifications() {
        notificationApi.getAllNotificationsForStudent(loggedInUser.getUsername()).enqueue(new Callback<List<Notification>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Notification>> call, @NonNull Response<List<Notification>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Notification> allNotifications = response.body();
                    notificationsList.clear();
                    notificationsList.addAll(allNotifications);
                    notificationAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(requireContext(), "Greska pri dohvatanju notifikacija!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notification>> call, @NonNull Throwable throwable) {
                Logger.getLogger(NotificationsFragment.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanje notifikacija nije uspeo!", throwable);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}