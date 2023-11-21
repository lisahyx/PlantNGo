package com.example.plantngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigationView);

        bottomNavigation.setOnItemSelectedListener(selectedListener);
        bottomNavigation.setSelectedItemId(R.id.home);
    }
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    CameraFragment cameraFragment = new CameraFragment();

    ProfileFragment profileFragment = new ProfileFragment();

    private BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, homeFragment)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.search) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, searchFragment)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.camera) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, cameraFragment)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.profile) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, profileFragment)
                        .commit();
                return true;
            } else {
                return false;
            }
        }
    };
}