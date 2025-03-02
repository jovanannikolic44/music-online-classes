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
        private final RetrofitService retrofitService = new RetrofitService();
        private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

        private User loggedInUser;
        private final ArrayList<TextView> profileInfoTextViews = new ArrayList<>();
        private final ArrayList<EditText> profileInfoEditTexts = new ArrayList<>();

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
            changeProfileInfo.setOnClickListener(view -> {
                makeFieldsEditable();
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
            // Drugacije ikonice u zavisnosti od user-a
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

            if("profesor".equals(loggedInUser.getType())){
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
            for(TextView textView : profileInfoTextViews) {
                EditText editText = new EditText(UserProfile.this);
                editText.setLayoutParams(textView.getLayoutParams());
                editText.setText(textView.getText());
//                editText.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_style));

                parentLayoutForProfileInfo.removeView(textView);
                parentLayoutForProfileInfo.addView(editText);
                profileInfoEditTexts.add(editText);
            }

            ImageButton changeProfileButton = findViewById(R.id.change_profile_info);
            changeProfileButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.save_icon));
            changeProfileButton.setOnClickListener(view -> {
                updateUserInformation();
            });

            ImageButton changeProfileBack = findViewById(R.id.change_profile_back);
            changeProfileBack.setVisibility(View.VISIBLE);
            changeProfileBack.setOnClickListener(view -> {
                switchBackToTextViewsWithNoChanges(parentLayoutForProfileInfo);
                changeProfileButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.change_profile_info));
                changeProfileBack.setVisibility(View.GONE);
            });

        }

        private void updateUserInformation() {

        }

        private void switchBackToTextViewsWithNoChanges(ConstraintLayout parentLayout) {
            for(EditText editText : profileInfoEditTexts) {
                parentLayout.removeView(editText);
            }
            for(TextView textView : profileInfoTextViews) {
                parentLayout.addView(textView);
            }
        }

        // Profilu dodati u log out
    }
