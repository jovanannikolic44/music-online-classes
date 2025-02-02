package com.masterprojekat.music_online_classes;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Preferences extends AppCompatActivity {
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
    }

    private void captureClickedPreferences() {
        GridLayout instrumentsLayout = findViewById(R.id.instruments_layout);

        for(int i = 0; i < instrumentsLayout.getChildCount(); i++) {
            View view = instrumentsLayout.getChildAt(i);
            if(view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;

                ImageView imageView = null;
                TextView textView = null;
                switch (i) {
                    case 0:
                        imageView = linearLayout.findViewById(R.id.guitar_image);
                        textView = linearLayout.findViewById(R.id.guitar_text);
                        break;
                    case 1:
                        imageView = linearLayout.findViewById(R.id.violin_image);
                        textView = linearLayout.findViewById(R.id.violin_text);
                        break;
                    case 2:
                        imageView = linearLayout.findViewById(R.id.piano_image);
                        textView = linearLayout.findViewById(R.id.piano_text);
                        break;
                    case 3:
                        imageView = linearLayout.findViewById(R.id.harmonica_image);
                        textView = linearLayout.findViewById(R.id.harmonica_text);
                        break;
                    case 4:
                        imageView = linearLayout.findViewById(R.id.trumpet_image);
                        textView = linearLayout.findViewById(R.id.trumpet_text);
                        break;
                    case 5:
                        imageView = linearLayout.findViewById(R.id.saxophone_image);
                        textView = linearLayout.findViewById(R.id.saxophone_text);
                        break;
                    case 6:
                        imageView = linearLayout.findViewById(R.id.drums_image);
                        textView = linearLayout.findViewById(R.id.drums_text);
                        break;
                    case 7:
                        imageView = linearLayout.findViewById(R.id.singing_image);
                        textView = linearLayout.findViewById(R.id.singing_text);
                        break;
                }

                if(imageView == null)
                    return;
                final String instrmentName = (String) imageView.getTag();
                TextView finalTextView = textView;
                imageView.setOnClickListener(v -> {
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