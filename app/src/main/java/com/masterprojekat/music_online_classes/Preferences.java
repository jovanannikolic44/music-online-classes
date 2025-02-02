package com.masterprojekat.music_online_classes;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

import java.util.Arrays;
import java.util.List;

public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int statusBarColor = ContextCompat.getColor(this, R.color.black);
        window.setStatusBarColor(statusBarColor);

        setInstrumentsInLayout();
    }

    private void setInstrumentsInLayout() {
//        GridLayout instrumentsLayout = findViewById(R.id.instruments_layout);
//        List<String> instruments = Arrays.asList("Elektricna gitara", "Akusticna gitara", "Violina",
//                "Harfa", "Klavir", "Harmonika", "Truba", "Saksofon", "Bubanj", "Pevanje");
//
//        for (String instrument : instruments) {
//            Chip chip = new Chip(this);
//            chip.setText(instrument);
//            chip.setCheckable(true);
//            chip.setChipCornerRadius(40);
//            chip.setMinHeight(80);
//            chip.setTextSize(18);
//            chip.setPadding(24, 8, 24, 8);
//            instrumentsLayout.addView(chip);
//        }
    }
}