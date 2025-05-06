package com.masterprojekat.music_online_classes.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.AddNewTerms;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.DateTimeFormatParser;
import com.masterprojekat.music_online_classes.helpers.ReservationsAdapter;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.helpers.Spinners;
import com.masterprojekat.music_online_classes.helpers.TermAdapter;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.TermStatus;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationsFragment extends Fragment {
    private static final String TAG = "ClassesFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private User loggedInUser;
    private final List<Term> reservationsList = new ArrayList<>();
    private ReservationsAdapter reservationsAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("Calling Resevations fragment");

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;
            setUpReservationsAdapter(view);

            EditText reservationDate = view.findViewById(R.id.reservation_date);
            Spinners.showDateSpinner(getContext(), reservationDate);

            Button chooseReservationDate = view.findViewById(R.id.choose_reservation_date);
            chooseReservationDate.setOnClickListener(localView -> {
                String chosenDate = String.valueOf(reservationDate.getText());
                String chosenDateNewFormat = DateTimeFormatParser.changeDateFormatTo(chosenDate, "dd-MM-yyyy", "yyyy-MM-dd");
                displayReservations(chosenDateNewFormat);
            });
        });
    }

    public void displayReservations(String chosenDate) {
        termApi.getTermsByDate(loggedInUser.getUsername(), loggedInUser.getType(), chosenDate, TermStatus.ZAHTEV_POSLAT).enqueue(new Callback<List<Term>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Term>> call, @NonNull Response<List<Term>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Term> termsResponseList = response.body();
                    reservationsList.clear();
                    reservationsList.addAll(termsResponseList);
                    reservationsAdapter.notifyDataSetChanged();
                }
                else {
                    Log.w(TAG, "Dohvatanje rezervacija nije uspesno: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Term>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem rezervacija nije uspeo!", throwable);
            }
        });
    }

    private void setUpReservationsAdapter(View view) {
        RecyclerView reservationsRecyclerView = view.findViewById(R.id.reservations_recycler_view);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reservationsAdapter = new ReservationsAdapter(reservationsList);
        reservationsRecyclerView.setAdapter(reservationsAdapter);
        reservationsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reservations_fragment, container, false);
    }
}
