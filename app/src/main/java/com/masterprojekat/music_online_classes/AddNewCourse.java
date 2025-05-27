package com.masterprojekat.music_online_classes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.helpers.Spinners;
import com.masterprojekat.music_online_classes.models.Course;
import com.masterprojekat.music_online_classes.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCourse extends AppCompatActivity {
    private static final String TAG = "AddNewCourse";
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private String courseInstrument = "";
    private String courseLevel = "";
    private User loggedInUser;
    private Uri selectedImageUri;
    private ImageView imagePreview;

    private ActivityResultLauncher<Intent> pickCourseImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);
        getWindow().setStatusBarColor(getResources().getColor(R.color.violet, getTheme()));
        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), decorView);
        insetsController.setAppearanceLightStatusBars(true);

        Intent intent = getIntent();
        loggedInUser = (User) intent.getSerializableExtra("loggedInUser");

        if(loggedInUser == null)
            return;

        setUpCourseLevelSpinner();
        setUpCourseInstrumentSpinner();
        chooseImage();

        Button addNewCourseButton = findViewById(R.id.add_new_course_button);
        addNewCourseButton.setOnClickListener(view -> {
            addNewCourse();
        });
    }

    private void chooseImage() {
        Button selectImageButton = findViewById(R.id.select_image_button);
        imagePreview = findViewById(R.id.selected_image_preview);
        pickCourseImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                imagePreview.setImageURI(selectedImageUri);
            }
        });
        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickCourseImageLauncher.launch(intent);
        });
    }

    private void addNewCourse() {
        String inputCourseName = getInputText(R.id.input_course_name);
        String inputCoursePrice = getInputText(R.id.input_course_price);
        String inputCourseClassesNumber = getInputText(R.id.input_course_classes_number);
        String inputCourseDescription = getInputText(R.id.input_course_description);
        String inputCourseContent = getInputText(R.id.input_course_content);

        if(inputCourseName.isEmpty() || inputCoursePrice.isEmpty()) {
            Toast.makeText(this, "Ime kursa i cena su obavezna polja!", Toast.LENGTH_SHORT).show();
            return;
        }

        float coursePrice = convertInputPriceToFloat(inputCoursePrice);
        int numberOfClasses = convertInputNumberOfClassesToInt(inputCourseClassesNumber);

        if (coursePrice < 0 || numberOfClasses < 0) {
            return;
        }

        Course newCourse = new Course(loggedInUser, inputCourseName, courseLevel, courseInstrument, inputCourseDescription, coursePrice, adaptContentString(inputCourseContent), numberOfClasses);
        uploadCourseWithImage(newCourse);
    }

    private void uploadCourseWithImage(Course newCourse) {
        File imageFile = new File(getCacheDir(), "_image.jpg");
        try(InputStream in = getContentResolver().openInputStream(selectedImageUri);
            FileOutputStream out = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(getContentResolver().getType(selectedImageUri))), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
        RequestBody name = RequestBody.create(MultipartBody.FORM, newCourse.getName());
        RequestBody price = RequestBody.create(MultipartBody.FORM, String.valueOf(newCourse.getPrice()));
        RequestBody professorUsername = RequestBody.create(MultipartBody.FORM, newCourse.getProfessor().getUsername());
        RequestBody level = RequestBody.create(MultipartBody.FORM, newCourse.getLevel());
        RequestBody instrument = RequestBody.create(MultipartBody.FORM, newCourse.getInstrument());
        RequestBody description = RequestBody.create(MultipartBody.FORM, newCourse.getDescription());
        RequestBody content = RequestBody.create(MultipartBody.FORM, newCourse.getContent());
        RequestBody numberOfClasses = RequestBody.create(MultipartBody.FORM, String.valueOf(newCourse.getNumberOfClasses()));

        saveCourse(name, price, professorUsername, level, instrument, description, content, numberOfClasses, imagePart);
    }

    private void saveCourse(RequestBody name, RequestBody price, RequestBody professorUsername, RequestBody level, RequestBody instrument, RequestBody description, RequestBody content, RequestBody numberOfClasses, MultipartBody.Part imagePart) {
        courseApi.addCourse(name, price, professorUsername, level, instrument, description, content, numberOfClasses, imagePart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(AddNewCourse.this, "Zahtev za dodavanjem kursa je uspesan!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Zahtev za dodavanjem novog kursa nije uspesan!", throwable);
            }
        });
    }

    private void setUpCourseLevelSpinner() {
        Spinner inputCourseLevelSpinner = findViewById(R.id.input_course_level);
        Spinners.showLevelSpinner(AddNewCourse.this, inputCourseLevelSpinner);

        inputCourseLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                courseLevel = "Nivo nije selektovan!";
            }
        });
    }

    private void setUpCourseInstrumentSpinner() {
        Spinner inputCourseInstrumentSpinner = findViewById(R.id.input_course_instrument);
        Spinners.showInstrumentSpinner(AddNewCourse.this, inputCourseInstrumentSpinner);

        inputCourseInstrumentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseInstrument = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                courseInstrument = "Instrument nije selektovan!";
            }
        });
    }

    private String getInputText(int id) {
        EditText editText = findViewById(id);
        return String.valueOf(editText.getText()).trim();
    }

    private String adaptContentString(String content) {
        String[] lines = content.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(line.trim());
            }
        }
        return sb.toString();
    }

    private float convertInputPriceToFloat(String price) {
        float coursePrice = 0f;
        try {
            coursePrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cena mora biti broj!", Toast.LENGTH_SHORT).show();
            return - 1;
        }
        return coursePrice;
    }

    private int convertInputNumberOfClassesToInt(String numberOfClasses) {
        int courseClassesNumber = 0;
        try {
            courseClassesNumber = Integer.parseInt(numberOfClasses);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Broj casova mora biti broj!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return courseClassesNumber;
    }
}