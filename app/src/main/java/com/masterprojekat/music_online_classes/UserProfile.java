    package com.masterprojekat.music_online_classes;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.ImageButton;
    import android.widget.ImageView;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    public class UserProfile extends AppCompatActivity {
        private Uri imageUri;

        // Profile picture from gallery
        private final ActivityResultLauncher<Intent> pickImageFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                ImageView profilePicture = findViewById(R.id.profile_picture);
                profilePicture.setImageURI(imageUri);
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

            // Choose image from gallery
            ImageView profilePicture = findViewById(R.id.profile_picture);
            ImageButton changeImage = findViewById(R.id.camera_button);

            changeImage.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageFromGallery.launch(intent);
            });
        }
    }