package com.example.plantngo.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass for managing user settings.
 */
public class SettingsFragment extends Fragment {

    // Firebase authentication instance
    FirebaseAuth fAuth = LoginActivity.fAuth;

    // Alert dialog builder for user deletion confirmation
    AlertDialog.Builder delete_alert;

    /**
     * Default constructor for the {@link SettingsFragment}.
     * Required empty public constructor.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The inflated view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Button to delete user account
        Button deleteUserButton = view.findViewById(R.id.delete_user_button);

        delete_alert = new AlertDialog.Builder(requireContext());

        // Set onClickListener for the delete user button
        deleteUserButton.setOnClickListener(v -> delete_alert.setTitle("Delete Account Permanently?")
                .setMessage("Are you sure?")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    // Delete the user account permanently
                    Objects.requireNonNull(fAuth.getCurrentUser()).delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                // Sign out and navigate to the login activity
                                fAuth.signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                            })
                            .addOnFailureListener(e -> {
                                // Display an error message if the deletion fails
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show());

        return view;
    }
}
