package com.example.plantngo.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.settings.SettingsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment representing the user profile with options to logout and navigate to settings.
 */
public class ProfileFragment extends Fragment {

    private Button logoutButton;
    private ImageButton settingsButton;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutButton = view.findViewById(R.id.logout_button);
        settingsButton = view.findViewById(R.id.settings_button);

        setupLogoutButton();
        setupSettingsButton();

        return view;
    }

    /**
     * Set up the click listener for the logout button.
     * Handles user logout and navigation to the login screen.
     */
    private void setupLogoutButton() {
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            GoogleSignIn.getClient(
                    requireContext(),
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut().addOnSuccessListener(unused -> {
                Toast.makeText(requireContext(), "User Logged Out", Toast.LENGTH_SHORT).show();
            });

            startActivity(new Intent(requireContext(), LoginActivity.class));
        });
    }

    /**
     * Set up the click listener for the settings button.
     * Navigates to the settings fragment when clicked.
     */
    private void setupSettingsButton() {
        settingsButton.setOnClickListener(v -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_profile, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}
