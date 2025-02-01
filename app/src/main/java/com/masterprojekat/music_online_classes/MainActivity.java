package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.models.User;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button newAccountButton = findViewById(R.id.new_account);
        newAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
        });

        TextView forgetPassword = findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(view -> {
            Intent forgetPasswordIntent = new Intent(this, PasswordReset.class);
            startActivity(forgetPasswordIntent);
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            login();
        });
    }

    public void login() {
        EditText inputUsername = (EditText) findViewById(R.id.username);
        EditText inputPassword = (EditText) findViewById(R.id.password);
        String username = String.valueOf(inputUsername.getText());
        String password = String.valueOf(inputPassword.getText());

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Popute sva prazna polja.", Toast.LENGTH_SHORT).show();
            return;
        }

        userApi.getUserByUsername(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()) {
                    User user = response.body();
                    if(user == null) {
                        Toast.makeText(MainActivity.this, "Korisnicko ime nije validno!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!password.equals(user.getPassword())) {
                        Toast.makeText(MainActivity.this, "Lozinka nije validna!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent preferencesIntent = new Intent(MainActivity.this, Preferences.class);
                    startActivity(preferencesIntent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Korisnicko ime nije validno!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                Toast.makeText(MainActivity.this, "Greska! Zahtev za prijavom nije uspeo!", Toast.LENGTH_SHORT).show();
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Greska! Zahtev za prijavom nije uspeo!", throwable);
            }
        });


    }
}
