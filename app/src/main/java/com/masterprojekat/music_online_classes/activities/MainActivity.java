package com.masterprojekat.music_online_classes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.UserAPI;
import com.masterprojekat.music_online_classes.models.User;
import com.masterprojekat.music_online_classes.models.UserAccountStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button newAccountButton = findViewById(R.id.new_account);
        newAccountButton.setOnClickListener(view -> {
            Intent registrationIntent = new Intent(this, RegistrationActivity.class);
            startActivity(registrationIntent);
        });

        TextView forgetPassword = findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(view -> {
            Intent forgetPasswordIntent = new Intent(this, PasswordResetActivity.class);
            startActivity(forgetPasswordIntent);
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            login();
        });
    }

    public void login() {
        EditText inputUsername = findViewById(R.id.username);
        EditText inputPassword = findViewById(R.id.password);
        String username = String.valueOf(inputUsername.getText());
        String password = String.valueOf(inputPassword.getText());

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Popute sva prazna polja.", Toast.LENGTH_SHORT).show();
            return;
        }

        userApi.getUserByUsername(username).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user == null) {
                        Toast.makeText(MainActivity.this, "Korisnicko ime nije validno!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.equals(user.getPassword())) {
                        Toast.makeText(MainActivity.this, "Lozinka nije validna!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (UserAccountStatus.NIJE_AKTIVAN.equals(user.getAccountStatus())) {
                        Toast.makeText(MainActivity.this, "Vas nalog jos uvek nije aktiviran!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (user.isFirstLogIn() && user.getType().equals("Ucenik")) {
                        Intent preferencesIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                        preferencesIntent.putExtra("loggedInUser", user);
                        startActivity(preferencesIntent);
                    } else {
                        switch (user.getType()) {
                            case "Ucenik":
                                Intent userProfileIntent = new Intent(MainActivity.this, StudentHomeActivity.class);
                                userProfileIntent.putExtra("loggedInUser", user);
                                startActivity(userProfileIntent);
                                break;
                            case "Profesor":
                                Intent professorProfileIntent = new Intent(MainActivity.this, ProfessorHomeActivity.class);
                                professorProfileIntent.putExtra("loggedInUser", user);
                                startActivity(professorProfileIntent);
                                break;
                            case "Admin":
                                Intent adminProfileIntent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                adminProfileIntent.putExtra("loggedInUser", user);
                                startActivity(adminProfileIntent);
                                break;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Korisnicko ime nije validno!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za prijavom nije uspeo!", throwable);
            }
        });
    }
}
