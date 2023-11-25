package com.example.plantngo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//import retrofit2.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView plantReminderRecyclerView;
    private RecyclerView gardenPlantsRecyclerView;
    private PlantReminderAdapter plantReminderAdapter;
    private GardenPlantsAdapter gardenPlantsAdapter;
    private List<Plant> plants;
    private static String JSON_URL = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        plantReminderRecyclerView = view.findViewById(R.id.recyclerViewPlants);
        gardenPlantsRecyclerView = view.findViewById(R.id.recyclerViewGardenPlants);
        plants = new ArrayList<>();

        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.api_output);
        //setRecyclerView(jsonContent);

        sharedPreferencesRecycler();

        return view;
    }

    public void sharedPreferencesRecycler() {
        // Get plant names from SharedPreferences
        SharedPreferencesStorage storage = new SharedPreferencesStorage();
        List<String> plantNames = storage.getPlantNamesFromSharedPreferences(requireContext());

        // Create a list to hold Plant objects
        plants = new ArrayList<>();

        // Create Plant objects with plant names
        for (String plantName : plantNames) {
            Plant plant = new Plant();
            plant.plantName = plantName;
            plants.add(plant);
        }

        // Create and set the adapter
        plantReminderAdapter = new PlantReminderAdapter(requireContext(), plants);
        gardenPlantsAdapter = new GardenPlantsAdapter(requireContext(), plants);

        // Assuming plantRecyclerView is the RecyclerView in your layout
        plantReminderRecyclerView.setAdapter(plantReminderAdapter);
        gardenPlantsRecyclerView.setAdapter(gardenPlantsAdapter);

        // Set a LinearLayoutManager
        plantReminderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        gardenPlantsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
    }
}