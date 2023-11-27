package com.example.plantngo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

public class PlantDetailsFragment extends Fragment {

    private ImageView openCalendar;
    private TextView plantNameView, sunlightInfo, waterInfo;

    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        plantNameView = view.findViewById(R.id.plantNameTextView);
        sunlightInfo = view.findViewById(R.id.sunlightInfo);
        waterInfo = view.findViewById(R.id.waterInfo);

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

        getSunlight();
        getWateringInfo();

        return view;
    }

    public void getSunlight() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_care_api_output);
        if (jsonContent != null) {
            try {
                String sunlight = jsonReader.parseSunlightJson(jsonContent);

                if (sunlight != null) {
                    sunlightInfo.setText(sunlight);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getWateringInfo() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_care_api_output);

        if (jsonContent != null) {
            try {
                String wateringInfo = jsonReader.parseWateringJson(jsonContent);

                if (wateringInfo != null) {
                    waterInfo.setText(wateringInfo);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}