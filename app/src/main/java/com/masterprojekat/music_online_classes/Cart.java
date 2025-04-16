package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.helpers.CartAdapter;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("loggedInUser")) {
            loggedInUser = (User) intent.getSerializableExtra("loggedInUser");
        }
        displayCoursesFromCart();
    }

    private void displayCoursesFromCart() {
        List<Course> cartCoursesList = new ArrayList<>();
        RecyclerView cartCourses = findViewById(R.id.cart_recycler_view);
        cartCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        CartAdapter cartAdapter = new CartAdapter(this, cartCoursesList);
        cartCourses.setAdapter(cartAdapter);
        cartCourses.setVisibility(View.VISIBLE);

        getCoursesFromCart(cartAdapter, cartCoursesList);
    }

    private void getCoursesFromCart(CartAdapter cartAdapter, List<Course> cartCoursesList) {
        userApi.getCoursesFromCart(loggedInUser.getUsername()).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if(response.isSuccessful()) {
                    List<Course> courses = response.body();
                    cartCoursesList.clear();
                    if(courses == null) {
                        Toast.makeText(Cart.this, "Nije nadjen ni jedan kurs!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cartCoursesList.addAll(courses);
                    cartAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem kurseva iz korpe nije uspeo!", throwable);
            }
        });
    }
}
