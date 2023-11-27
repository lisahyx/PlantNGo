package com.example.plantngo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PlantDetailsFragment extends Fragment {

    private ImageView openCalendar;
    private TextView plantNameView;

    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        plantNameView = view.findViewById(R.id.plantNameTextView);

        openCalendar = view.findViewById(R.id.calenderImageView);

        openCalendar.setOnClickListener(view1 -> {
            CalendarFragment calendarFragment = new CalendarFragment();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.plant_details, calendarFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Retrieve data from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String plantName = bundle.getString("plantName");
            // display plant name in text view
            plantNameView.setText(plantName);
        }

        return view;
    }
}