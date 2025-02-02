package com.masterprojekat.music_online_classes;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.masterprojekat.music_online_classes.APIs.PasswordResetAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.helpers.Validation;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordUpdate extends AppCompatActivity {
    private final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final RetrofitService retrofitService = new RetrofitService();
    private final PasswordResetAPI passwordResetApi = retrofitService.getRetrofit().create(PasswordResetAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int statusBarColor = ContextCompat.getColor(this, R.color.black);
        window.setStatusBarColor(statusBarColor);

        Uri data = getIntent().getData();
        String token = null;
        if (data != null) {
            token = data.getQueryParameter("token");
            if (token == null) {
                Toast.makeText(PasswordUpdate.this, "Link nije ispravan", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        updatePassword(token);
    }

    private void updatePassword(String token) {
        EditText newPassword = findViewById(R.id.new_password);
        EditText confirmNewPassword = findViewById(R.id.confirm_new_password);
        Button resetButton = findViewById(R.id.reset_password_button);

        resetButton.setOnClickListener(view -> {
            String password = String.valueOf(newPassword.getText());
            String confirmPassword = String.valueOf(confirmNewPassword.getText());

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(PasswordUpdate.this, "Popunite obavezna polja!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(PasswordUpdate.this, "Lozinke nisu iste!", Toast.LENGTH_SHORT).show();
                return;
            }
            Validation.validateUserInput(PASSWORD_REGEX, password, "Lozinka mora da ima najmanje 8 karaktera, bar 1 veliko slovo, bar 1 malo slovo, bar 1 broj i bar 1 specijalan karakter.");
            updatePasswordApiCall(token, password);
        });
    }

    private void updatePasswordApiCall(String token, String password) {
        passwordResetApi.updatePassword(token, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PasswordUpdate.this, "Lozinka je uspesno azurirana!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PasswordUpdate.this, "Greska prilikom azuriranja lozinke!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Toast.makeText(PasswordUpdate.this, "Greska pri slanju zahteva za azuriranje lozinke!", Toast.LENGTH_SHORT).show();
                Logger.getLogger(PasswordUpdate.class.getName()).log(Level.SEVERE, "Greska pri slanju zahteva za azuriranje lozinke!", throwable);
            }
        });
    }
}
