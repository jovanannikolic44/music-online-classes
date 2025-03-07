    package com.masterprojekat.music_online_classes;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.Window;
    import android.view.WindowManager;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.lifecycle.ViewModelProvider;

    import com.masterprojekat.music_online_classes.databinding.ActivityUserProfileBinding;
    import com.masterprojekat.music_online_classes.fragments.ClassesFragment;
    import com.masterprojekat.music_online_classes.fragments.CoursesFragment;
    import com.masterprojekat.music_online_classes.fragments.NotificationsFragment;
    import com.masterprojekat.music_online_classes.fragments.ProfileFragment;
    import com.masterprojekat.music_online_classes.fragments.StatisticsFragment;
    import com.masterprojekat.music_online_classes.helpers.SharedViewModel;
    import com.masterprojekat.music_online_classes.models.User;

    public class UserProfile extends AppCompatActivity {

        ActivityUserProfileBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            System.out.println("Calling onCreate in UserProfile");
            super.onCreate(savedInstanceState);
            binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
            EdgeToEdge.enable(this);
            setContentView(binding.getRoot());

            replaceFragment(new ProfileFragment());

            // Get logged in user
            Intent userIntent = getIntent();
            User loggedInUser = (User) userIntent.getSerializableExtra("loggedInUser");
            if(loggedInUser == null)
                return;

            // Give logged in user to Fragment
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            viewModel.setUser(loggedInUser);


            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                if(item.getItemId() == R.id.profile_nav) {
                    replaceFragment(new ProfileFragment());
                }
                else if(item.getItemId() == R.id.statistics_nav) {
                    replaceFragment(new StatisticsFragment());
                }
                else if(item.getItemId() == R.id.classes_nav) {
                    replaceFragment(new ClassesFragment());
                }
                else if(item.getItemId() == R.id.courses_nav) {
                    replaceFragment(new CoursesFragment());
                }
                else if(item.getItemId() == R.id.notifications_nav) {
                    replaceFragment(new NotificationsFragment());
                }
                return true;
            });

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int statusBarColor = ContextCompat.getColor(this, R.color.black);
            window.setStatusBarColor(statusBarColor);
        }

        private void replaceFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }
