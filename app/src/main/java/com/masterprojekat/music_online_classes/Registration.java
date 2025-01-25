package com.masterprojekat.music_online_classes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// *** NOTES: Layout scrollable, text colour to be black
public class Registration extends AppCompatActivity {

    final Calendar calendar = Calendar.getInstance();
    EditText input_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        showDateSpinner();

        register_new_user();
    }

    private void register_new_user() {
        EditText inputName = findViewById(R.id.input_name);
        EditText inputSurname = findViewById(R.id.input_surname);
        EditText inputUsername = findViewById(R.id.input_username);
        EditText inputPassword = findViewById(R.id.input_password);
        EditText inputDate = findViewById(R.id.input_date);
        EditText inputEmail = findViewById(R.id.input_email);
        EditText inputPhoneNumber = findViewById(R.id.input_phone_number);
        RadioGroup inputType = findViewById(R.id.radio_type);
        EditText inputEducation = findViewById(R.id.input_education);
        Spinner inputExpertise = findViewById(R.id.input_expertise);
        Button registrationButton = findViewById(R.id.registration_button);

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

        registrationButton.setOnClickListener(view -> {
            String name = String.valueOf(inputName.getText());
            String surname = String.valueOf(inputSurname.getText());
            String username = String.valueOf(inputUsername.getText());
            String password = String.valueOf(inputPassword.getText());
            String date = String.valueOf(inputDate.getText());
            String email = String.valueOf(inputEmail.getText());
            String phoneNumber = String.valueOf(inputPhoneNumber.getText());
            // radio button
            String type = "ucenik";
            String education = String.valueOf(inputEducation.getText());
            // spinners
            String expertise = "";

            User user = new User(name, surname, username, password, date, email, phoneNumber, type, education, expertise);

            userApi.saveUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    Toast.makeText(Registration.this, "User saved successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    Toast.makeText(Registration.this, "User saving failed!", Toast.LENGTH_SHORT).show();
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, "Error occurred!", throwable);
                }
            });
        });
    }

    private void showDateSpinner() {
        input_date = findViewById(R.id.input_date);
        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);

                        String format = "dd-MM-yyyy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
                        input_date.setText(dateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}