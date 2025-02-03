package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.masterprojekat.music_online_classes.APIs.PasswordResetAPI;
import com.masterprojekat.music_online_classes.APIs.PreferencesAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.models.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Preferences extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final PreferencesAPI preferencesAPI = retrofitService.getRetrofit().create(PreferencesAPI.class);
    private final Set<String> clickedInstruments = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int statusBarColor = ContextCompat.getColor(this, R.color.black);
        window.setStatusBarColor(statusBarColor);

        captureClickedPreferences();

        Button savePreferences = findViewById(R.id.continue_button);
        savePreferences.setOnClickListener(view -> saveSelectedPreferences());
    }

    private void saveSelectedPreferences() {
        Intent intent = getIntent();
        User loggedInUser = (User) intent.getSerializableExtra("loggedInUser");
        if(loggedInUser == null)
            return;
        preferencesAPI.savePreferences(loggedInUser.getUsername(), clickedInstruments).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Preferences.this, "Sacuvane preferencije!", Toast.LENGTH_SHORT).show();
                    // Go to user profile
                } else {
                    Toast.makeText(Preferences.this, "Neuspesno cuvanje preferencija!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Toast.makeText(Preferences.this, "Greska! Neuspesan zahtev za cuvanje preferencija!", Toast.LENGTH_SHORT).show();
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, "Greska! Neuspesan zahtev za cuvanje preferencija!", throwable);
            }
        });
    }

    private void captureClickedPreferences() {
        GridLayout instrumentsLayout = findViewById(R.id.instruments_layout);
        int[] imageViewIds = {
                R.id.guitar_image, R.id.violin_image, R.id.piano_image, R.id.harmonica_image,
                R.id.trumpet_image, R.id.saxophone_image, R.id.drums_image, R.id.singing_image
        };

        int[] textViewIds = {
                R.id.guitar_text, R.id.violin_text, R.id.piano_text, R.id.harmonica_text,
                R.id.trumpet_text, R.id.saxophone_text, R.id.drums_text, R.id.singing_text
        };

        for(int i = 0; i < instrumentsLayout.getChildCount(); i++) {
            View view = instrumentsLayout.getChildAt(i);
            if(view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                ImageView imageView = null;
                TextView textView = null;

                if (i < imageViewIds.length) {
                    imageView = linearLayout.findViewById(imageViewIds[i]);
                    textView = linearLayout.findViewById(textViewIds[i]);
                }

                if(imageView == null)
                    return;

                final String instrmentName = (String) imageView.getTag();
                TextView finalTextView = textView;
                imageView.setOnClickListener(v -> {
                    System.out.println("Instrument name " + instrmentName);
                    if(clickedInstruments.contains(instrmentName)) {
                        clickedInstruments.remove(instrmentName);
                    }
                    else {
                        clickedInstruments.add(instrmentName);
                    }

                    if (finalTextView.getTypeface().isBold()) {
                        finalTextView.setTypeface(null, Typeface.NORMAL);
                        finalTextView.setTextSize(11);
                    } else {
                        finalTextView.setTypeface(null, Typeface.BOLD);
                        finalTextView.setTextSize(14);
                    }

                });
            }
        }
    }
}