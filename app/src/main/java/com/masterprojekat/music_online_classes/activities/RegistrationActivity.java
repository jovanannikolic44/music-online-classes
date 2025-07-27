package com.masterprojekat.music_online_classes.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.UserAPI;
import com.masterprojekat.music_online_classes.utils.Spinners;
import com.masterprojekat.music_online_classes.utils.Validation;
import com.masterprojekat.music_online_classes.models.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final String PHONE_NUMBER_REGEX = "^\\+381\\d{8,9}$";
    private String expertise = "";

    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        EditText inputDate = (EditText) findViewById(R.id.input_date);
        Spinners.showDateSpinner(RegistrationActivity.this, inputDate);

        Spinner inputExpertiseSpinner = (Spinner) findViewById(R.id.input_expertise);
        Spinners.showInstrumentSpinner(RegistrationActivity.this, inputExpertiseSpinner);

        registerNewUser();
    }

    private void registerNewUser() {
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

        inputExpertise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expertise = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expertise = "Ekspertiza nije selektovana!";
            }
        });

        inputType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            String type = String.valueOf(selectedButton.getText());
            if ("Profesor".equals(type)) {
                inputEducation.setVisibility(View.VISIBLE);
                inputExpertise.setVisibility(View.VISIBLE);
            } else {
                inputEducation.setVisibility(View.GONE);
                inputExpertise.setVisibility(View.GONE);
                expertise = "Nije selektovan";
            }
        });

        registrationButton.setOnClickListener(view -> {
            String name = String.valueOf(inputName.getText());
            String surname = String.valueOf(inputSurname.getText());
            String username = String.valueOf(inputUsername.getText());
            String password = String.valueOf(inputPassword.getText());
            String date = String.valueOf(inputDate.getText());
            String email = String.valueOf(inputEmail.getText());
            String phoneNumber = String.valueOf(inputPhoneNumber.getText());
            RadioButton typeRadioButton = findViewById(inputType.getCheckedRadioButtonId());
            String type = String.valueOf(typeRadioButton.getText());
            String education = String.valueOf(inputEducation.getText());
            if(name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Ime, prezime, korisnicko ime, lozinka i email su obavezna polja!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Validation.validateUserInput(EMAIL_REGEX, email, "Neispravan email format.");
                Validation.validateUserInput(PASSWORD_REGEX, password, "Lozinka mora da ima najmanje 8 karaktera, bar 1 veliko slovo, bar 1 malo slovo, bar 1 broj i bar 1 specijalan karakter.");
                Validation.validateUserInput(PHONE_NUMBER_REGEX, phoneNumber,"Broj telefona mora biti u formatu +381, sa 8 ili 9 dodatnih cifara.");
            } catch(IllegalArgumentException e) {
                Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            userApi.getUserByUsername(username).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (!response.isSuccessful()) {
                        userApi.checkEmailAndPhoneNumberUniqueness(email, phoneNumber).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                if (!response.isSuccessful()) {
                                    try {
                                        if (response.errorBody() == null) {
                                            Logger.getLogger(RegistrationActivity.class.getName()).log(Level.SEVERE, "Greska pri dohvatanju errorBody()");
                                            return;
                                        }
                                        String errorMessage = response.errorBody().string();
                                        Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    User user = new User(name, surname, username, password, date, email, phoneNumber, type, education, expertise, true);
                                    userApi.saveUser(user).enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                            Toast.makeText(RegistrationActivity.this, "Zahtev za registraciju uspesno poslat!", Toast.LENGTH_SHORT).show();
                                            inputName.setText("");
                                            inputSurname.setText("");
                                            inputUsername.setText("");
                                            inputPassword.setText("");
                                            inputDate.setText("");
                                            inputEmail.setText("");
                                            inputPhoneNumber.setText("");
                                            inputEducation.setText("");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                                            Log.e(TAG, "Greska pri slanju zahteva za registraciju!", throwable);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                                Log.e(TAG, "Greska pri slanju zahteva za proveru jedinstvenosti email-a i broja telefona!", throwable);
                            }
                        });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Korisnicko ime je zauzeto.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    Log.e(TAG, "Greska pri slanju zahteva za proveru jedinstvenosti email-a i broja telefona!", throwable);
                }
            });
        });
    }
}
