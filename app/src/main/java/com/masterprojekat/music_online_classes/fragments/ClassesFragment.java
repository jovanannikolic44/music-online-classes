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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.AddNewTerms;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.helpers.DateTimeFormatParser;
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

public class ClassesFragment extends Fragment {
    private static final String TAG = "ClassesFragment";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private User loggedInUser;
    private final List<Term> scheduledTermsList = new ArrayList<>();
    private TermAdapter termAdapter;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null)
                return;
            loggedInUser = user;
            setUpTermAdapter(view);

            EditText scheduledTermsDate = view.findViewById(R.id.scheduled_terms_date);
            Spinners.showDateSpinner(getContext(), scheduledTermsDate);

            Button chooseDateButton = view.findViewById(R.id.choose_date_button);
            chooseDateButton.setOnClickListener(localView -> {
                String chosenDate = String.valueOf(scheduledTermsDate.getText());
                String chosenDateNewFormat = DateTimeFormatParser.changeDateFormatTo(chosenDate, "dd-MM-yyyy","yyyy-MM-dd");
                displayTerms(chosenDateNewFormat);
            });

            if("Profesor".equals(user.getType())) {
                ImageButton addNewTerms = view.findViewById(R.id.add_new_terms);
                addNewTerms.setVisibility(View.VISIBLE);
                addNewTerms.setOnClickListener(localView -> {
                    Intent addNewTermssIntent = new Intent(getContext(), AddNewTerms.class);
                    addNewTermssIntent.putExtra("loggedInUser", loggedInUser);
                    requireActivity().startActivity(addNewTermssIntent);
                });
            }
        });
    }

    private void displayTerms(String chosenDate) {
        termApi.getTermsByDate(loggedInUser.getUsername(), loggedInUser.getType(), chosenDate, TermStatus.PRIHVACEN).enqueue(new Callback<List<Term>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Term>> call, @NonNull Response<List<Term>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Term> termsResponseList = response.body();
                    scheduledTermsList.clear();
                    scheduledTermsList.addAll(termsResponseList);
                    termAdapter.notifyDataSetChanged();

                    if(termsResponseList.isEmpty()) {
                        Toast.makeText(getContext(), "Ne postoji ni jedan termin za izabrani datum!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.w(TAG, "Dohvatanje zakazanih termina nije uspesno: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Term>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem odabranih termina nije uspeo!", throwable);
            }
        });
    }

    private void setUpTermAdapter(View view) {
        RecyclerView termRecyclerView = view.findViewById(R.id.scheduled_terms_recycler_view);
        termRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        termAdapter = new TermAdapter(scheduledTermsList, loggedInUser);
        termRecyclerView.setAdapter(termAdapter);
        termRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }
}
