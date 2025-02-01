package com.masterprojekat.music_online_classes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.APIs.PasswordResetAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordUpdate extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final PasswordResetAPI passwordResetApi = retrofitService.getRetrofit().create(PasswordResetAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_update);

        String token = Objects.requireNonNull(getIntent().getData()).getQueryParameter("token");
        if(token == null) {
            Toast.makeText(PasswordUpdate.this, "Link nije ispravan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updatePassword(token);
    }

    private void updatePassword(String token) {
        EditText newPassword = findViewById(R.id.newPassword);
        EditText confirmNewPassword = findViewById(R.id.confirmNewPassword);
        Button resetButton = findViewById(R.id.resetPasswordButton);

        resetButton.setOnClickListener(view -> {
            String password = newPassword.getText().toString();
            String confirmPassword = confirmNewPassword.getText().toString();

            if(password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(PasswordUpdate.this, "Popunite obavezna polja!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!password.equals(confirmPassword)) {
                Toast.makeText(PasswordUpdate.this, "Lozinke nisu iste!", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePasswordApiCall(token, password);
        });
    }

    private void updatePasswordApiCall(String token, String password) {
        passwordResetApi.updatePassword(token, password).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PasswordUpdate.this, "Lozinka je uspesno azurirana!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PasswordUpdate.this, "Greska prilikom azuriranja lozinke!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                Toast.makeText(PasswordUpdate.this, "Greska pri slanju zahteva za azuriranje lozinke!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}