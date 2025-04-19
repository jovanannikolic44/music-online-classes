package com.masterprojekat.music_online_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);
    private CartAdapter cartAdapter;
    private List<Course> cartCoursesList = new ArrayList<>();
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
        setUpAdapter();
        getCoursesFromCart();

        Button buyCoursesButton = findViewById(R.id.buy_courses);
        buyCoursesButton.setOnClickListener(view -> {
            buyCourses();
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void buyCourses() {
        List<Course> selectedCourses = cartAdapter.getSelectedCourses();
        if (selectedCourses.isEmpty()) {
            Toast.makeText(Cart.this, "Niste izabrali nijedan kurs.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> purchasedCoursesIds = new ArrayList<>();
        for (Course course : selectedCourses) {
            int courseId = course.getCourseId();
            purchaseCourse(courseId);
            purchasedCoursesIds.add(courseId);
        }
        cartCoursesList.removeAll(selectedCourses);
        cartAdapter.notifyDataSetChanged();

        float newTotal = cartAdapter.calculateSelectedTotal();
        TextView totalPriceTextView = findViewById(R.id.total_price);
        totalPriceTextView.setText("RSD " + newTotal);

        removeCoursesFromCart(purchasedCoursesIds);
    }

    private void removeCoursesFromCart(List<Integer> purchasedIds) {
        userApi.removeCoursesFromCart(loggedInUser.getUsername(), purchasedIds).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Logger.getLogger(Cart.class.getName()).log(Level.INFO, "Kurs je uspesno obrisan!");
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, "Greska! Zahtev za brisanje kurseva nije uspeo!", throwable);
            }
        });
    }

    private void purchaseCourse(int courseId) {
        userApi.purchaseCourse(loggedInUser.getUsername(), courseId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Toast.makeText(Cart.this, "Kurs je uspesno kupljen!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, "Greska! Zahtev za kupovinom kursa nije uspeo!", throwable);
            }
        });
    }

    private void setUpAdapter() {
        RecyclerView cartCourses = findViewById(R.id.cart_recycler_view);
        cartCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cartAdapter = new CartAdapter(this, cartCoursesList);
        cartAdapter.setOnSelectionChangedListener(totalPrice -> {
            TextView totalPriceTextView = findViewById(R.id.total_price);
            totalPriceTextView.setText("RSD " + totalPrice);
        });
        cartCourses.setAdapter(cartAdapter);
        cartCourses.setVisibility(View.VISIBLE);
    }

    private void getCoursesFromCart() {
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

                    // Initial total calculation (in case any course is already selected)
                    float initialTotal = cartAdapter.calculateSelectedTotal();
                    TextView totalPriceTextView = findViewById(R.id.total_price);
                    totalPriceTextView.setText("RSD " + initialTotal);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable throwable) {
                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem kurseva iz korpe nije uspeo!", throwable);
            }
        });
    }
}
