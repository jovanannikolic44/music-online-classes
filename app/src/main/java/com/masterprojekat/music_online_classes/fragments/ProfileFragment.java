package com.masterprojekat.music_online_classes.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.UserAPI;
import com.masterprojekat.music_online_classes.MainActivity;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.UserProfile;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.helpers.Spinners;
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

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public class ProfileFragment extends Fragment {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final String PHONE_NUMBER_REGEX = "^\\+381\\d{8,9}$";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userApi = retrofitService.getRetrofit().create(UserAPI.class);

    private final ActivityResultLauncher<Intent> pickImageFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            ImageView profilePicture = requireView().findViewById(R.id.profile_picture);
            profilePicture.setImageURI(imageUri);
            uploadProfilePictureToServer(imageUri);
        }
    });

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            if (loggedInUser == null)
                return;

            getProfilePicture(view, loggedInUser);
            displayProfileInformation(view, loggedInUser);

            ImageButton changeImage = requireView().findViewById(R.id.camera_button);
            changeImage.setOnClickListener(viewLocal -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageFromGallery.launch(galleryIntent);
            });

            Button changeProfileInformation = requireView().findViewById(R.id.change_profile_information);
            changeProfileInformation.setOnClickListener(viewLocal -> {
                enterNewProfileInfo(view, loggedInUser);
            });

            Button changePasswordButton = requireView().findViewById(R.id.change_password);
            changePasswordButton.setOnClickListener(viewLocal -> {
                enterNewPassword(loggedInUser);
            });

            Button logOutButon = requireView().findViewById(R.id.log_out);
            logOutButon.setOnClickListener(viewLocal -> {
                Intent logOutIntent = new Intent(getContext(), MainActivity.class);
                logOutIntent.putExtra("loggedInUser", "");
                startActivity(logOutIntent);
            });
        });
    }

    private void uploadProfilePictureToServer(Uri imageUri) {
        File imageFile = new File(requireActivity().getCacheDir(), "profile_picture.jpg");
        try(InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Greska pri procesiranju fajla!", Toast.LENGTH_SHORT).show();
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(requireContext().getContentResolver().getType(imageUri))), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        sendPictureToServer(body);
    }

    private void sendPictureToServer(MultipartBody.Part body) {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        User loggedInUser = viewModel.getUser().getValue();
        if(loggedInUser == null)
            return;

        userApi.uploadProfilePicture(body, loggedInUser.getUsername()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                System.out.println(response);
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Greška! Profilna slika nije sačuvana!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(ProfileFragment.class.getName()).log(Level.SEVERE, "Greška! Profilna slika nije dobro sačuvana na serveru!", throwable);
            }
        });
    }

    private void getProfilePicture(View view, User loggedInUser) {
        ImageView profilePicture = view.findViewById(R.id.profile_picture);

        if (loggedInUser == null) {
            Toast.makeText(requireContext(), "Greška! Korisnik nije pronađen!", Toast.LENGTH_SHORT).show();
            return;
        }

        userApi.getProfilePicture(loggedInUser.getUsername()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    File profilePictureFile = new File(requireActivity().getCacheDir(), "profile_picture.jpg");
                    try (FileOutputStream outputStream = new FileOutputStream(profilePictureFile)) {
                        outputStream.write(response.body().bytes());

                        Glide.with(requireContext())
                                .load(profilePictureFile)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profilePicture);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Greška pri dohvatanju fajla!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Greška! Profilna slika nije preuzeta!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(ProfileFragment.class.getName()).log(Level.SEVERE, "Greška! Profilna slika nije dohvaćena sa servera!", throwable);
            }
        });
    }

    private void displayProfileInformation(View view, User loggedInUser) {
        TextView label_username = view.findViewById(R.id.label_username);
        TextView label_name = view.findViewById(R.id.label_name);
        TextView label_surname = view.findViewById(R.id.label_surname);
        TextView label_birth_date = view.findViewById(R.id.label_birth_date);
        TextView label_email = view.findViewById(R.id.label_email);
        TextView label_phone_number = view.findViewById(R.id.label_phone_number);
        TextView label_education = view.findViewById(R.id.label_education);
        TextView label_expertise = view.findViewById(R.id.label_expertise);

        label_username.setText(loggedInUser.getUsername());
        label_name.setText(loggedInUser.getName());
        label_surname.setText(loggedInUser.getSurname());
        label_birth_date.setText(loggedInUser.getDate());
        label_email.setText(loggedInUser.getEmail());
        label_phone_number.setText(loggedInUser.getPhoneNumber());

        if ("Profesor".equals(loggedInUser.getType())) {
            label_education.setText(loggedInUser.getEducation());
            label_expertise.setText(loggedInUser.getExpertise());
            label_education.setVisibility(View.VISIBLE);
            label_expertise.setVisibility(View.VISIBLE);
        } else {
            label_education.setVisibility(View.GONE);
            label_expertise.setVisibility(View.GONE);
        }
    }

    private void enterNewProfileInfo(View view, User loggedInUser) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_profile_info_dialog, null);

        EditText nameInput = dialogView.findViewById(R.id.change_name);
        nameInput.setText(loggedInUser.getName());
        EditText surnameInput = dialogView.findViewById(R.id.change_surname);
        surnameInput.setText(loggedInUser.getSurname());
        EditText emailInput = dialogView.findViewById(R.id.change_email);
        emailInput.setText(loggedInUser.getEmail());
        EditText dateInput = dialogView.findViewById(R.id.change_date);
        dateInput.setText(loggedInUser.getDate());
        EditText phoneInput = dialogView.findViewById(R.id.change_phone_number);
        phoneInput.setText(loggedInUser.getPhoneNumber());

        Spinners.showDateSpinner(requireContext(), dateInput);

        if ("Profesor".equals(loggedInUser.getType())) {
            EditText educationInput = dialogView.findViewById(R.id.change_education);
            educationInput.setText(loggedInUser.getEducation());
            Spinner expertiseInput = dialogView.findViewById(R.id.change_expertise);
            Spinners.showExpertiseSpinner(requireContext(), expertiseInput);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Promena informacija na profilu")
                .setView(dialogView)
                .setPositiveButton("Sacuvaj", null)
                .setNegativeButton("Otkazi", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set background color for the dialog window
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4BEE4")));
        }

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String surname = surnameInput.getText().toString();
            String date = dateInput.getText().toString();
            String email = emailInput.getText().toString();
            String phone_number = phoneInput.getText().toString();

            try {
                Validation.validateUserInput(EMAIL_REGEX, email, "Neispravan email format.");
                Validation.validateUserInput(PHONE_NUMBER_REGEX, phone_number, "Neispravan format za broj telefona.");
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            loggedInUser.setName(name);
            loggedInUser.setSurname(surname);
            loggedInUser.setDate(date);
            loggedInUser.setEmail(email);
            loggedInUser.setPhoneNumber(phone_number);

            userApi.updateUserInfo(loggedInUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<User> call, @NonNull retrofit2.Response<User> response) {
                    dialog.dismiss();
                    displayProfileInformation(view, loggedInUser);
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<User> call, @NonNull Throwable throwable) {
                    Logger.getLogger(ProfileFragment.class.getName()).log(Level.SEVERE, "Greska! Korisnicki podaci nisu uspesno azurirani!", throwable);
                }
            });
        });
    }

    private void enterNewPassword(User loggedInUser) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_password_dialog, null);

        EditText oldPasswordInput = dialogView.findViewById(R.id.old_password_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        EditText confirmNewPasswordInput = dialogView.findViewById(R.id.new_password_confirm_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Promena lozinke")
                .setView(dialogView)
                .setPositiveButton("Sacuvaj", null)
                .setNegativeButton("Otkazi", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4BEE4")));
        }

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String confirmNewPassword = confirmNewPasswordInput.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Popunite sva prazna polja.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!loggedInUser.getPassword().equals(oldPassword)) {
                Toast.makeText(requireContext(), "Stara lozinka je pogresno uneta!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Validation.validateUserInput(PASSWORD_REGEX, newPassword, "Lozinka mora da ima najmanje 8 karaktera, bar 1 veliko slovo, bar 1 malo slovo, bar 1 broj i bar 1 specijalan karakter.");
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!confirmNewPassword.equals(newPassword)) {
                Toast.makeText(requireContext(), "Lozinke se ne poklapaju!", Toast.LENGTH_SHORT).show();
                return;
            }

            userApi.updateUserPassword(loggedInUser.getUsername(), newPassword).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<User> call, @NonNull retrofit2.Response<User> response) {
                    Toast.makeText(requireContext(), "Lozinka je azurirana!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<User> call, @NonNull Throwable throwable) {
                    Toast.makeText(requireContext(), "Greska pri azuriranju lozinke!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}