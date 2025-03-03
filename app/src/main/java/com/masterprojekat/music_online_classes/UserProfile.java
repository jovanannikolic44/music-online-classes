    package com.masterprojekat.music_online_classes;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.util.TypedValue;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.core.content.ContextCompat;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.engine.DiskCacheStrategy;
    import com.masterprojekat.music_online_classes.APIs.RetrofitService;
    import com.masterprojekat.music_online_classes.APIs.UserAPI;
    import com.masterprojekat.music_online_classes.helpers.Validation;
    import com.masterprojekat.music_online_classes.models.User;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.util.ArrayList;
    import java.util.Objects;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    import okhttp3.MediaType;
    import okhttp3.MultipartBody;
    import okhttp3.RequestBody;
    import okhttp3.ResponseBody;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class UserProfile extends AppCompatActivity {
        private final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        private final String PHONE_NUMBER_REGEX = "^\\+381\\d{8,9}$";
        private final String DATE_REGEX = "^\\d{2}-\\d{2}-\\d{4}$";
        private final RetrofitService retrofitService = new RetrofitService();
        private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

        private User loggedInUser;
        private final ArrayList<TextView> profileInfoTextViews = new ArrayList<>();
        private final ArrayList<EditText> profileInfoEditTexts = new ArrayList<>();
        private boolean isEditable = false;

        // Profile picture from gallery
        private final ActivityResultLauncher<Intent> pickImageFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                ImageView profilePicture = findViewById(R.id.profile_picture);
                profilePicture.setImageURI(imageUri);
                uploadProfilePictureToServer(imageUri);
            }
        });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_user_profile);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int statusBarColor = ContextCompat.getColor(this, R.color.black);
            window.setStatusBarColor(statusBarColor);

            Intent userIntent = getIntent();
            loggedInUser = (User) userIntent.getSerializableExtra("loggedInUser");
            if(loggedInUser == null)
                return;

            getProfilePicture();
            displayProfileInformation();

            // Choose image from gallery
            ImageButton changeImage = findViewById(R.id.camera_button);
            changeImage.setOnClickListener(view -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageFromGallery.launch(galleryIntent);
            });

            // Change user data
            ImageButton changeProfileInfo = findViewById(R.id.change_profile_info);
            ImageButton changeProfileBack = findViewById(R.id.change_profile_back);
            changeProfileInfo.setOnClickListener(view -> {
                if (isEditable) {
                    updateUserInformation();
                } else {
                    makeFieldsEditable();
                    changeProfileInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.save_icon));
                    changeProfileBack.setVisibility(View.VISIBLE);
                }
            });
            changeProfileBack.setOnClickListener(view -> {
                switchBackToTextViewsWithNoChanges();
                isEditable = false;
                changeProfileInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.change_profile_info));
                changeProfileBack.setVisibility(View.GONE);
            });
        }
        private void uploadProfilePictureToServer(Uri imageUri) {
            File imageFile = new File(getCacheDir(), "profile_picture.jpg");
            try(InputStream inputStream = getContentResolver().openInputStream(imageUri);
                FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch(IOException e) {
                e.printStackTrace();
                Toast.makeText(UserProfile.this, "Greska pri procesiranju fajla!", Toast.LENGTH_SHORT).show();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(getContentResolver().getType(imageUri))), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            sendPictureToServer(body);
        }

        private void sendPictureToServer(MultipartBody.Part body) {
            userApi.uploadProfilePicture(body, loggedInUser.getUsername()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    System.out.println(response);
                    if (!response.isSuccessful()) {
                        Toast.makeText(UserProfile.this, "Greska! Profilna slika nije sacuvana!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    Logger.getLogger(UserProfile.class.getName()).log(Level.SEVERE, "Greska! Profilna slika nije dobro sacuvana na serveru!", throwable);
                }
            });
        }

        private void getProfilePicture() {
            ImageView profilePicture = findViewById(R.id.profile_picture);
            userApi.getProfilePicture(loggedInUser.getUsername()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        File profilePictureFile = new File(getCacheDir(), "profile_picture.jpg");
                        try(FileOutputStream outputStream = new FileOutputStream(profilePictureFile)) {
                            outputStream.write(response.body().bytes());

                            Glide.with(UserProfile.this).load(profilePictureFile)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true ).into(profilePicture);

                        } catch(IOException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfile.this, "Greska pri dohvatanju fajla!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    Logger.getLogger(UserProfile.class.getName()).log(Level.SEVERE, "Greska! Profilna slika nije dobro dohvatcena sa servera!", throwable);
                }
            });
        }

        private void displayProfileInformation() {
            TextView label_username = findViewById(R.id.label_username);
            TextView label_name = findViewById(R.id.label_name);
            profileInfoTextViews.add(label_name);
            TextView label_surname = findViewById(R.id.label_surname);
            profileInfoTextViews.add(label_surname);
            TextView label_birth_date = findViewById(R.id.label_birth_date);
            profileInfoTextViews.add(label_birth_date);
            TextView label_email = findViewById(R.id.label_email);
            profileInfoTextViews.add(label_email);
            TextView label_phone_number = findViewById(R.id.label_phone_number);
            profileInfoTextViews.add(label_phone_number);
            TextView label_education = findViewById(R.id.label_education);
//            profileInfoTextViews.add(label_education);
            TextView label_expertise = findViewById(R.id.label_expertise);
//            profileInfoTextViews.add(label_expertise);

            label_username.setText(loggedInUser.getUsername());
            label_name.setText(loggedInUser.getName());
            label_surname.setText(loggedInUser.getSurname());
            label_birth_date.setText(loggedInUser.getDate());
            label_email.setText(loggedInUser.getEmail());
            label_phone_number.setText(loggedInUser.getPhoneNumber());

            if("Profesor".equals(loggedInUser.getType())){
                label_education.setText(loggedInUser.getEducation());
                label_expertise.setText(loggedInUser.getExpertise());
                label_education.setVisibility(TextView.VISIBLE);
                label_expertise.setVisibility(TextView.VISIBLE);
            }
            else {
                label_education.setVisibility(TextView.GONE);
                label_expertise.setVisibility(TextView.GONE);
            }
        }

        private void makeFieldsEditable() {
            ConstraintLayout parentLayoutForProfileInfo = findViewById(R.id.profile_body);
            for (TextView textView : profileInfoTextViews) {
                EditText editText = new EditText(UserProfile.this);
                editText.setLayoutParams(textView.getLayoutParams());
                editText.setText(textView.getText());
                parentLayoutForProfileInfo.removeView(textView);
                parentLayoutForProfileInfo.addView(editText);
                profileInfoEditTexts.add(editText);
            }
            isEditable = true;
        }

        private void updateUserInformation() {
            boolean allFieldsValid = true;
            ImageButton changeProfileButton = findViewById(R.id.change_profile_info);
            ImageButton changeProfileBack = findViewById(R.id.change_profile_back);

            for (int i = 0; i < profileInfoEditTexts.size(); i++) {
                EditText editText = profileInfoEditTexts.get(i);
                String updatedValue = String.valueOf(editText.getText());
                try {
                    switch (i) {
                        case 0:
                            loggedInUser.setName(updatedValue);
                            break;
                        case 1:
                            loggedInUser.setSurname(updatedValue);
                            break;
                        case 2:
                            Validation.validateUserInput(DATE_REGEX, updatedValue, "Neispravan format za datum.");
                            loggedInUser.setDate(updatedValue);
                            break;
                        case 3:
                            Validation.validateUserInput(EMAIL_REGEX, updatedValue, "Neispravan email format.");
                            loggedInUser.setEmail(updatedValue);
                            break;
                        case 4:
                            Validation.validateUserInput(PHONE_NUMBER_REGEX, updatedValue, "Neispravan format za broj telefona.");
                            loggedInUser.setPhoneNumber(updatedValue);
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    allFieldsValid = false;
                }
            }

            if (allFieldsValid) {
                userApi.updateUserInfo(loggedInUser).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        switchBackToTextViews();
                        changeProfileButton.setImageDrawable(ContextCompat.getDrawable(UserProfile.this, R.drawable.change_profile_info));
                        changeProfileBack.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                        Logger.getLogger(UserProfile.class.getName()).log(Level.SEVERE, "Greska! Korisnicki podaci nisu uspesno azurirani!", throwable);
                    }
                });
            } else {
                isEditable = true;
                changeProfileButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.save_icon));
                changeProfileBack.setVisibility(View.VISIBLE);
            }
        }


        private void switchBackToTextViews() {
            ConstraintLayout parentLayout = findViewById(R.id.profile_body);
            profileInfoTextViews.clear();

            for(int i = 0; i < profileInfoEditTexts.size(); i++) {
                EditText editText = profileInfoEditTexts.get(i);
                TextView textView = new TextView(UserProfile.this);

                textView.setLayoutParams(editText.getLayoutParams());
                textView.setText(editText.getText().toString());
                textView.setTextSize(18);

                parentLayout.removeView(editText);
                parentLayout.addView(textView);
                profileInfoTextViews.add(textView);
            }
            profileInfoEditTexts.clear();
        }

        private void switchBackToTextViewsWithNoChanges() {
            ConstraintLayout parentLayout = findViewById(R.id.profile_body);
            for(EditText editText : profileInfoEditTexts) {
                parentLayout.removeView(editText);
            }
            for(TextView textView : profileInfoTextViews) {
                parentLayout.addView(textView);
            }
            profileInfoEditTexts.clear();
        }

        // Profilu dodati u log out
    }
