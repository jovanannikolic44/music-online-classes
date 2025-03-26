package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

public class CourseDetails extends AppCompatActivity {
    // Ako je kurs kupljen onda ima i input polje za komentare
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
    }

    private void displayCourseDetails() {
        // get all simple fields
        TextView courseNameView = findViewById(R.id.course_name);
        TextView courseProfessorView = findViewById(R.id.course_professor);
        TextView courseLevelView = findViewById(R.id.course_level_value);
        TextView courseInstrumentView = findViewById(R.id.course_instrument_value);
        TextView courseRatingView = findViewById(R.id.course_rating_value);
        TextView coursePriceView = findViewById(R.id.course_price_value);
        TextView courseDescriptionView = findViewById(R.id.course_description_value);
    }
}