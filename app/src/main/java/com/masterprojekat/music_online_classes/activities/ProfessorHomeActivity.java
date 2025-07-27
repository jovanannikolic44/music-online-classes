package com.masterprojekat.music_online_classes.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.databinding.ActivityProfessorHomeFragmentsBinding;
import com.masterprojekat.music_online_classes.fragments.ClassesFragment;
import com.masterprojekat.music_online_classes.fragments.ProfessorCoursesFragment;
import com.masterprojekat.music_online_classes.fragments.ProfileFragment;
import com.masterprojekat.music_online_classes.fragments.ReservationsFragment;
import com.masterprojekat.music_online_classes.utils.SharedViewModel;
import com.masterprojekat.music_online_classes.models.User;

public class ProfessorHomeActivity extends AppCompatActivity {

    ActivityProfessorHomeFragmentsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessorHomeFragmentsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        replaceFragment(new ProfileFragment());

        Intent userIntent = getIntent();
        User loggedInUser = (User) userIntent.getSerializableExtra("loggedInUser");
        if(loggedInUser == null)
            return;

        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setUser(loggedInUser);

        binding.professorBottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.profile_nav) {
                replaceFragment(new ProfileFragment());
            }
            else if(item.getItemId() == R.id.reservations_nav) {
                replaceFragment(new ReservationsFragment());
            }
            else if(item.getItemId() == R.id.terms_nav) {
                replaceFragment(new ClassesFragment());
            }
            else if(item.getItemId() == R.id.courses_nav) {
                replaceFragment(new ProfessorCoursesFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}