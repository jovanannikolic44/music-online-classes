package com.masterprojekat.music_online_classes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.TermAPI;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReserveTermActivity extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private User loggedInUser;
    private Course courseToReserve;
    private Term selectedTerm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserve_term);

        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("loggedInUser");
        courseToReserve = (Course) intent.getSerializableExtra("courseToReserve");
        if(loggedInUser == null || courseToReserve == null)
            return;

        displayAvailableTerms();
    }

    private void displayAvailableTerms() {
        Spinner termSpinner = findViewById(R.id.term_spinner);
        Button reserveTermButton = findViewById(R.id.reserve_term_button);
        termApi.getAllAvailableTermsForProfessor(courseToReserve.getProfessor().getUsername()).enqueue(new Callback<List<Term>>() {
            @Override
            public void onResponse(@NonNull Call<List<Term>> call, @NonNull Response<List<Term>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Term> availableTerms = response.body();

                    ArrayAdapter<Term> adapter = new ArrayAdapter<>(ReserveTermActivity.this, android.R.layout.simple_spinner_item, availableTerms);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    termSpinner.setAdapter(adapter);

                    getSelectedTermFromSpinner(termSpinner, availableTerms);
                    reserveTermButton.setOnClickListener(view -> {
                        if (selectedTerm != null) {
                            reserveTerm(availableTerms, adapter);
                        } else {
                            Toast.makeText(ReserveTermActivity.this, "Niste izabrali ni jedan termin!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(ReserveTermActivity.this, "Greska pri dohvatanju slobodnih termina!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Term>> call, @NonNull Throwable throwable) {
                Logger.getLogger(ReserveTermActivity.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem slobodnih termina nije uspeo!", throwable);
            }
        });
    }

    private void reserveTerm(List<Term> availableTerms, ArrayAdapter<Term> adapter) {
        termApi.requestTerm(selectedTerm.getTermId(), loggedInUser.getUsername(), courseToReserve.getCourseId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ResponseBody responseBody = response.body();
                    String message = null;
                    try {
                        message = responseBody.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(ReserveTermActivity.this, message, Toast.LENGTH_SHORT).show();

                    availableTerms.remove(selectedTerm);
                    adapter.notifyDataSetChanged();
                    selectedTerm = null;
                }
                else {
                    Toast.makeText(ReserveTermActivity.this, "Greska pri rezervaciji termina!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(ReserveTermActivity.class.getName()).log(Level.SEVERE, "Greska! Zahtev za rezervaciju termina nije uspeo!", throwable);
            }
        });
    }

    private void getSelectedTermFromSpinner(Spinner termSpinner, List<Term> availableTerms) {
        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTerm = availableTerms.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTerm = null;
            }
        });
    }
}