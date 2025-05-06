package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.helpers.DateTimeFormatParser;
import com.masterprojekat.music_online_classes.helpers.Spinners;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.TermStatus;
import com.masterprojekat.music_online_classes.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewTerms extends AppCompatActivity {
    private static final String TAG = "AddNewTerms";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_terms);

        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("loggedInUser");

        if(loggedInUser == null)
            return;

        EditText inputNewTermDate = findViewById(R.id.scheduled_terms_date);
        Spinners.showDateSpinner(AddNewTerms.this, inputNewTermDate);

        EditText inputNewTermTime = findViewById(R.id.scheduled_terms_time);
        Spinners.showTimeSpinner(AddNewTerms.this, inputNewTermTime);

        Button addNewTermButton = findViewById(R.id.add_new_terms_button);
        addNewTermButton.setOnClickListener(view -> {
            getSelectedDateAndTime();
        });
    }

    public void getSelectedDateAndTime() {
        EditText inputNewTermDate = findViewById(R.id.scheduled_terms_date);
        String newTermDate = String.valueOf(inputNewTermDate.getText());

        EditText inputNewTermTime = findViewById(R.id.scheduled_terms_time);
        String newTermTime = String.valueOf(inputNewTermTime.getText());

        String formattedNewTermDate = DateTimeFormatParser.changeDateFormatTo(newTermDate, "dd-MM-yyyy", "yyyy-MM-dd");
        String formattedNewTermTime = DateTimeFormatParser.changeTimeFormatTo(newTermTime, "HH:mm", "HH:mm:ss");

        Term newTerm = new Term();
        newTerm.setDate(formattedNewTermDate);
        newTerm.setTime(formattedNewTermTime);
        newTerm.setProfessor(loggedInUser);

        termApi.createNewTerm(newTerm).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddNewTerms.this, "Termin uspesno dodat!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddNewTerms.this, "Termin nije dodat!", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri dodavanju novog termina!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Dodavanje termina nije uspelo!", throwable);
            }
        });
    }
}