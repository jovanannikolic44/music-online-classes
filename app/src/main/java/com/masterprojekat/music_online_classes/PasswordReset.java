package com.masterprojekat.music_online_classes;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.masterprojekat.music_online_classes.APIs.PasswordResetAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.helpers.Validation;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordReset extends AppCompatActivity {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final RetrofitService retrofitService = new RetrofitService();
    private final PasswordResetAPI passwordResetApi = retrofitService.getRetrofit().create(PasswordResetAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int statusBarColor = ContextCompat.getColor(this, R.color.black);
        window.setStatusBarColor(statusBarColor);

        resetForgottenPassword();
    }

    private void resetForgottenPassword() {
        EditText inputEmail = (EditText) findViewById(R.id.reactivation_email);
        Button sendEmail = (Button) findViewById(R.id.change_forgotten_password);

        sendEmail.setOnClickListener(view -> {
            String toEmail = String.valueOf(inputEmail.getText());
            if(toEmail.isEmpty()) {
                Toast.makeText(this, "Email je obavezno polje!", Toast.LENGTH_SHORT).show();
                return;
            }
            Validation.validateUserInput(EMAIL_REGEX, toEmail, "Neispravan email format.");

            passwordResetApi.requestPasswordReset(toEmail).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Toast.makeText(PasswordReset.this, "Mail je uspesno poslat!", Toast.LENGTH_LONG).show();
                    inputEmail.setText("");
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                    Toast.makeText(PasswordReset.this, "Greska! Mejl nije poslat!", Toast.LENGTH_LONG).show();
                    Logger.getLogger(PasswordReset.class.getName()).log(Level.SEVERE, "Greska! Mejl nije poslat!", throwable);
                    inputEmail.setText("");
                }
            });
        });
    }
}