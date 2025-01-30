package com.masterprojekat.music_online_classes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.masterprojekat.music_online_classes.APIs.EmailAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.helpers.Validation;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordReset extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final EmailAPI emailApi = retrofitService.getRetrofit().create(EmailAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        resetForgottenPassword();
    }

    private void resetForgottenPassword() {
        EditText inputEmail = (EditText) findViewById(R.id.reactivation_email);
        Button sendEmail = (Button) findViewById(R.id.change_forgotten_password);

        sendEmail.setOnClickListener(view -> {
            String toEmail = String.valueOf(inputEmail.getText());
            String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if(toEmail.isEmpty()) {
                Toast.makeText(this, "Email je obavezno polje!", Toast.LENGTH_SHORT).show();
                return;
            }

            Validation.validateUserInput(emailRegex, toEmail, "Neispravan email format.");

            // Poslati na mejl link sa formom za promenu lozinke
            String subjectEmail = "Resetovanje lozinke";
            String bodyEmail = "Forma za resetovanje lozinke - ";
            emailApi.sendEmail(toEmail, subjectEmail, bodyEmail).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(PasswordReset.this, "Email uspesno poslat", Toast.LENGTH_SHORT).show();
                    inputEmail.setText("");
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                    Logger.getLogger(PasswordReset.class.getName()).log(Level.SEVERE, "Greska pri slanju mejla");
                    inputEmail.setText("");
                }
            });
        });
    }
}