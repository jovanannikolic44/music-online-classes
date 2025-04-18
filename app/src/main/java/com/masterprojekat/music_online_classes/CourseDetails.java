package com.masterprojekat.music_online_classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.helpers.CommentAdapter;
import com.masterprojekat.music_online_classes.models.Comment;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetails extends AppCompatActivity {
    // Ako je kurs kupljen onda ima i input polje za komentare
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private User loggedInUser;
    private Course courseToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_details);

        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("loggedInUser");
        courseToDisplay = (Course) intent.getSerializableExtra("course");
        if(loggedInUser == null || courseToDisplay == null)
            return;
        displayCourseDetails();

        boolean isPurchased = checkIfCourseIsPurchased();
        Button addToCartButton = findViewById(R.id.add_course_to_cart);
        Button reserveTermButton = findViewById(R.id.reserve_lesson_term);
        if(!isPurchased) {
            addToCartButton.setVisibility(View.VISIBLE);
            reserveTermButton.setVisibility(View.GONE);
            addToCartButton.setOnClickListener(view -> {
                addCourseToCart();
            });
        }
        else {
            addToCartButton.setVisibility(View.GONE);
            reserveTermButton.setVisibility(View.VISIBLE);
            reserveTermButton.setOnClickListener(view -> {
                System.out.println("Rezervisanje termina!");
            });
        }

        ImageButton cartButton = findViewById(R.id.course_details_cart);
        cartButton.setOnClickListener(view -> {
            Intent cartIntent = new Intent(this, Cart.class);
            cartIntent.putExtra("loggedInUser", loggedInUser);
            startActivity(cartIntent);
        });
    }

    private boolean checkIfCourseIsPurchased() {
        // Check if courseToDisplay is purchased for user Username
        return true;
    }

    private void addCourseToCart() {
        userApi.addCourseToCart(loggedInUser.getUsername(), courseToDisplay.getCourseId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(CourseDetails.this, "Kurs uspesno dodat u korpu!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CourseDetails.this, "Greska! Kurs nije dodat u korpu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(CourseDetails.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dodavanjem kursa u korpu nije uspeo!", throwable);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayCourseDetails() {
        TextView courseNameView = findViewById(R.id.course_name);
        TextView courseProfessorView = findViewById(R.id.course_professor);
        TextView courseLevelView = findViewById(R.id.course_level_value);
        TextView courseInstrumentView = findViewById(R.id.course_instrument_value);
        TextView courseRatingView = findViewById(R.id.course_rating_value);
        TextView coursePriceView = findViewById(R.id.course_price_value);
        TextView courseDescriptionView = findViewById(R.id.course_description_value);

        displayCourseImage();
        displayCourseContent();
        courseNameView.setText(courseToDisplay.getName());
        courseProfessorView.setText(courseToDisplay.getProfessor().getName() + " " + courseToDisplay.getProfessor().getSurname());
        courseLevelView.setText(courseToDisplay.getLevel());
        courseInstrumentView.setText(courseToDisplay.getInstrument());
        courseRatingView.setText(String.valueOf(courseToDisplay.getRating()));
        coursePriceView.setText(String.valueOf(courseToDisplay.getPrice()));
        courseDescriptionView.setText(courseToDisplay.getDescription());

        displayCourseComments();
    }

    public void displayCourseComments() {
        RecyclerView commentsRecyclerView = findViewById(R.id.comments_list);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Comment> comments = courseToDisplay.getComments();
        CommentAdapter commentAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    public void displayCourseContent() {
        TextView courseContentListView = findViewById(R.id.course_content_list);
        String courseContent = courseToDisplay.getContent();
        String[] contentList = courseContent.split(",");
        StringBuilder content = new StringBuilder();
        for (String item : contentList) {
            content.append("• ").append(item).append("\n");
        }
        courseContentListView.setText(content.toString());
    }


    public void displayCourseImage() {
        ImageView courseImageView = findViewById(R.id.course_image);
        String fullImagePath = courseToDisplay.getCourseImage();
        String imageFile = fullImagePath.substring(fullImagePath.lastIndexOf("/") + 1);
        courseApi.getCourseImage(imageFile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    Glide.with(CourseDetails.this)
                            .load(bitmap)
                            .into(courseImageView);
                } else {
                    courseImageView.setImageDrawable(ContextCompat.getDrawable(CourseDetails.this, R.drawable.broken_image));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(CourseDetails.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem slike kursa nije uspeo!", throwable);
            }
        });
    }
}