package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }

//    public void login(View view) {
//        String username = ((EditText) findViewById(R.id.username)).getText().toString();
//        String password = ((EditText) findViewById(R.id.password)).getText().toString();
//
//        if(username.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Popute sva prazna polja.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        User logged_in = null;
//
//    }

    public void register(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
}