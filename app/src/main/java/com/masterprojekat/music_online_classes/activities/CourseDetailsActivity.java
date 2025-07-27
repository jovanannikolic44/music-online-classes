package com.masterprojekat.music_online_classes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.api.CommentAPI;
import com.masterprojekat.music_online_classes.api.CourseAPI;
import com.masterprojekat.music_online_classes.api.CourseProgressAPI;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.UserAPI;
import com.masterprojekat.music_online_classes.adapters.CommentAdapter;
import com.masterprojekat.music_online_classes.models.Comment;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.CourseProgress;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailsActivity";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final CommentAPI commentAPI = retrofitService.getRetrofit().create(CommentAPI.class);
    private final CourseProgressAPI courseProgressApi = retrofitService.getRetrofit().create(CourseProgressAPI.class);
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
        getAllCommentsForCourse();
        addToCartOrReserveTerm();

        Button saveCommentButton = findViewById(R.id.course_comment_button);
        saveCommentButton.setOnClickListener(view -> {
            EditText commentInput = findViewById(R.id.course_comment_input);
            String commentText = String.valueOf(commentInput.getText());
            Comment comment = new Comment();
            comment.setAuthor(loggedInUser);
            comment.setCourse(courseToDisplay);
            comment.setText(commentText);
            saveComment(comment);
        });

        ImageButton cartButton = findViewById(R.id.course_details_cart);
        cartButton.setOnClickListener(view -> {
            Intent cartIntent = new Intent(this, CartActivity.class);
            cartIntent.putExtra("loggedInUser", loggedInUser);
            startActivity(cartIntent);
        });
    }

    private void saveComment(Comment commentToAdd) {
        commentAPI.saveComment(commentToAdd).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailsActivity.this, "Komentar uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    getAllCommentsForCourse();
                } else {
                    Toast.makeText(CourseDetailsActivity.this, "Greska pri cuvanju komentara!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za cuvanjem komentara nije uspeo!", throwable);
            }
        });
    }

    private void getAllCommentsForCourse() {
        commentAPI.getAllCommentsForCourse(courseToDisplay.getCourseId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseToDisplay.setComments(response.body());
                    displayCourseComments();
                } else {
                    Toast.makeText(CourseDetailsActivity.this, "Greska pri ucitavanju komentara!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanje komentara nije uspeo!", throwable);
            }
        });
    }

    private void addToCartOrReserveTerm() {
        userApi.isCoursePurchased(loggedInUser.getUsername(), courseToDisplay.getCourseId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if(response.isSuccessful() && response.body() != null) {
                    boolean isPurchased = response.body();
                    Button addToCartButton = findViewById(R.id.add_course_to_cart);
                    Button reserveTermButton = findViewById(R.id.reserve_lesson_term);
                    EditText courseCommentEditText = findViewById(R.id.course_comment_input);
                    Button saveCommentButton = findViewById(R.id.course_comment_button);
                    TextView commentsLabel = findViewById(R.id.course_comments_label);
                    RatingBar courseRatingBar = findViewById(R.id.course_rating_bar);
                    Button finishedCourseButton = findViewById(R.id.finished_course_button);
                    if(!isPurchased) {
                        addToCartButton.setVisibility(View.VISIBLE);
                        reserveTermButton.setVisibility(View.GONE);
                        courseCommentEditText.setVisibility(View.GONE);
                        saveCommentButton.setVisibility(View.GONE);
                        courseRatingBar.setVisibility(View.GONE);
                        finishedCourseButton.setVisibility(View.GONE);
                        addToCartButton.setOnClickListener(view -> {
                            addCourseToCart();
                        });
                    }
                    else {
                        courseProgressApi.getCourseProgress(courseToDisplay.getCourseId(), loggedInUser.getUsername()).enqueue(new Callback<CourseProgress>() {
                            @Override
                            public void onResponse(@NonNull Call<CourseProgress> call, @NonNull Response<CourseProgress> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    CourseProgress courseProgress = response.body();
                                    int progress = courseProgress.getProgress();
                                    int numberOfClasses = courseToDisplay.getNumberOfClasses();
                                    if(progress == numberOfClasses) {
                                        reserveTermButton.setVisibility(View.GONE);
                                        finishedCourseButton.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        finishedCourseButton.setVisibility(View.GONE);
                                        reserveTermButton.setVisibility(View.VISIBLE);
                                    }
                                    addToCartButton.setVisibility(View.GONE);
                                    courseCommentEditText.setVisibility(View.VISIBLE);
                                    saveCommentButton.setVisibility(View.VISIBLE);
                                    courseRatingBar.setVisibility(View.VISIBLE);
                                    courseRatingBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                                    courseRatingBar.setSecondaryProgressTintList(ColorStateList.valueOf(Color.GRAY));
                                    courseRatingBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) commentsLabel.getLayoutParams();
                                    layoutParams.topMargin = 700;
                                    commentsLabel.setLayoutParams(layoutParams);
                                    courseRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                                        if(fromUser) {
                                            saveRating(rating);
                                        }
                                    });
                                    if(progress < numberOfClasses) {
                                        reserveTermButton.setOnClickListener(view -> {
                                            Intent reserveTermIntent = new Intent(CourseDetailsActivity.this, ReserveTermActivity.class);
                                            reserveTermIntent.putExtra("loggedInUser", loggedInUser);
                                            reserveTermIntent.putExtra("courseToReserve", courseToDisplay);
                                            startActivity(reserveTermIntent);
                                        });
                                    }
                                }
                                else {
                                    Log.w(TAG, "Greska pri dohvatanju progresa!" + response.code());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<CourseProgress> call, @NonNull Throwable throwable) {
                                Log.e(TAG, "Greska! Dohvatanje progresa nije uspelo!", throwable);
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(CourseDetailsActivity.this, "Greška pri proveri kupovine kursa!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za proverom obavljene kupovine nije uspeo!", throwable);
            }
        });
    }

    private void getRating() {
        courseApi.getCourseRating(courseToDisplay.getCourseId()).enqueue(new Callback<>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<Float> call, @NonNull Response<Float> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final float newCourseRating = response.body();
                    courseToDisplay.setRating(newCourseRating);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView courseRatingView = findViewById(R.id.course_rating_value);
                            courseRatingView.setText(String.format("%.2f", newCourseRating));
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Float> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dodavanjem ocene nije uspeo!", throwable);
            }
        });
    }

    private void saveRating(float rating_input) {
        courseApi.saveCourseRating(courseToDisplay.getCourseId(), rating_input).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailsActivity.this, "Ocena je uspesno sacuvana!", Toast.LENGTH_SHORT).show();
                    getRating();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za cuvanjem ocene nije uspeo!", throwable);
            }
        });
    }

    private void addCourseToCart() {
        userApi.addCourseToCart(loggedInUser.getUsername(), courseToDisplay.getCourseId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailsActivity.this, "Kurs uspesno dodat u korpu!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CourseDetailsActivity.this, "Greska! Kurs nije dodat u korpu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dodavanjem kursa u korpu nije uspeo!", throwable);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void displayCourseDetails() {
        TextView courseNameView = findViewById(R.id.course_name);
        TextView courseProfessorView = findViewById(R.id.course_professor);
        TextView courseLevelView = findViewById(R.id.course_level_value);
        TextView courseInstrumentView = findViewById(R.id.course_instrument_value);
        TextView courseRatingView = findViewById(R.id.course_rating_value);
        TextView coursePriceView = findViewById(R.id.course_price_value);
        TextView courseDescriptionView = findViewById(R.id.course_description_value);
        TextView courseClassesNumberView = findViewById(R.id.course_classes_value);

        displayCourseImage();
        displayCourseContent();
        courseNameView.setText(courseToDisplay.getName());
        courseProfessorView.setText(courseToDisplay.getProfessor().getName() + " " + courseToDisplay.getProfessor().getSurname());
        courseLevelView.setText(courseToDisplay.getLevel());
        courseInstrumentView.setText(courseToDisplay.getInstrument());
        courseRatingView.setText(String.format("%.2f", courseToDisplay.getRating()));
        coursePriceView.setText(String.valueOf(courseToDisplay.getPrice()));
        courseDescriptionView.setText(courseToDisplay.getDescription());
        courseClassesNumberView.setText(String.valueOf(courseToDisplay.getNumberOfClasses()));
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
        courseApi.getCourseImage(imageFile).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    Glide.with(CourseDetailsActivity.this)
                            .load(bitmap)
                            .into(courseImageView);
                } else {
                    courseImageView.setImageDrawable(ContextCompat.getDrawable(CourseDetailsActivity.this, R.drawable.broken_image));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dohvatanjem slike kursa nije uspeo!", throwable);
            }
        });
    }
}
