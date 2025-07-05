package com.masterprojekat.music_online_classes;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.masterprojekat.music_online_classes.databinding.ActivityAdminHomeFragmentsBinding;
import com.masterprojekat.music_online_classes.fragments.NewCoursesFragment;
import com.masterprojekat.music_online_classes.fragments.NewUsersFragment;
import com.masterprojekat.music_online_classes.fragments.ProfileFragment;
import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
import com.masterprojekat.music_online_classes.models.User;

public class AdminHomeFragments extends AppCompatActivity {

    ActivityAdminHomeFragmentsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeFragmentsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        replaceFragment(new ProfileFragment());

        Intent userIntent = getIntent();
        User loggedInUser = (User) userIntent.getSerializableExtra("loggedInUser");
        if(loggedInUser == null)
            return;

        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setUser(loggedInUser);

        binding.adminBottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.profile_nav) {
                replaceFragment(new ProfileFragment());
            }
            else if(item.getItemId() == R.id.new_courses_nav) {
                replaceFragment(new NewCoursesFragment());
            }
            else if(item.getItemId() == R.id.new_users_nav) {
                replaceFragment(new NewUsersFragment());
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