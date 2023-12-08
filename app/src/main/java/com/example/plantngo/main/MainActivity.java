package com.example.plantngo.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.plantngo.R;
import com.example.plantngo.camera.CameraFragment;
import com.example.plantngo.authentication.ProfileFragment;
import com.example.plantngo.plant.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The main activity that hosts the bottom navigation and fragments.
 */
public class MainActivity extends AppCompatActivity {

    // Fragments
    private final HomeFragment homeFragment = new HomeFragment();
    private final CameraFragment cameraFragment = new CameraFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and set up the bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnItemSelectedListener(selectedListener);
        bottomNavigation.setSelectedItemId(R.id.home);
    }

    /**
     * Listener for handling item selection in the bottom navigation view.
     */
    private final BottomNavigationView.OnItemSelectedListener selectedListener = item -> {
        if (item.getItemId() == R.id.home) {
            replaceFragment(homeFragment);
            return true;
        } else if (item.getItemId() == R.id.camera) {
            replaceFragment(cameraFragment);
            return true;
        } else if (item.getItemId() == R.id.profile) {
            replaceFragment(profileFragment);
            return true;
        } else {
            return false;
        }
    };

    /**
     * Replace the current fragment with the given fragment and add the transaction to the back stack.
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }
}
