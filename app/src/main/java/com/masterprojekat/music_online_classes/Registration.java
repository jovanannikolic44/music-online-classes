package com.masterprojekat.music_online_classes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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


public class Registration extends AppCompatActivity {

    final Calendar calendar = Calendar.getInstance();
    EditText input_date;
    private String expertise = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        showDateSpinner();
        showExpertiseSpinner();

        register_new_user();
    }

    private void register_new_user() {
        EditText inputName = (EditText) findViewById(R.id.input_name);
        EditText inputSurname = (EditText) findViewById(R.id.input_surname);
        EditText inputUsername = (EditText) findViewById(R.id.input_username);
        EditText inputPassword = (EditText) findViewById(R.id.input_password);
        EditText inputDate = (EditText) findViewById(R.id.input_date);
        EditText inputEmail = (EditText) findViewById(R.id.input_email);
        EditText inputPhoneNumber = (EditText) findViewById(R.id.input_phone_number);
        RadioGroup inputType = (RadioGroup) findViewById(R.id.radio_type);
        EditText inputEducation = (EditText) findViewById(R.id.input_education);
        Spinner inputExpertise = (Spinner) findViewById(R.id.input_expertise);
        Button registrationButton = (Button) findViewById(R.id.registration_button);

        inputExpertise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expertise = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expertise = "Nije selektovan";
            }
        });

        inputType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = (RadioButton) findViewById(checkedId);
            String type = String.valueOf(selectedButton.getText());
            Toast.makeText(Registration.this, "RadioButton " + type, Toast.LENGTH_SHORT).show();
            if ("Profesor".equals(type)) {
                inputEducation.setVisibility(View.VISIBLE);
                inputExpertise.setVisibility(View.VISIBLE);
            } else {
                inputEducation.setVisibility(View.GONE);
                inputExpertise.setVisibility(View.GONE);
            }
        });

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
            RadioButton typeRadioButton = (RadioButton) findViewById(inputType.getCheckedRadioButtonId());
            String type = String.valueOf(typeRadioButton.getText());
            String education = String.valueOf(inputEducation.getText());

            User user = new User(name, surname, username, password, date, email, phoneNumber, type, education, expertise);

            userApi.saveUser(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    Toast.makeText(Registration.this, "Zahtev za registraciju uspesno poslat!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    Toast.makeText(Registration.this, "User saving failed!", Toast.LENGTH_SHORT).show();
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, "Greska!", throwable);
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

    private void showExpertiseSpinner() {
        Spinner inputExpertiseSpinner = (Spinner) findViewById(R.id.input_expertise);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.expertise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputExpertiseSpinner.setAdapter(adapter);
    }
}
