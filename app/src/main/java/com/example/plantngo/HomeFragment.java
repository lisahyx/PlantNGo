package com.example.plantngo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//import retrofit2.Response;

public class HomeFragment extends Fragment implements RecyclerViewInterface{
    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView plantReminderRecyclerView;
    private RecyclerView gardenPlantsRecyclerView;
    private FloatingActionButton addPlantButton;
    private PlantReminderAdapter plantReminderAdapter;
    private GardenPlantsAdapter gardenPlantsAdapter;
    private List<Plant> plants;
    private static String JSON_URL = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addPlantButton = view.findViewById(R.id.add_plant_button);

        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlant();
            }
        });

        plantReminderRecyclerView = view.findViewById(R.id.recyclerViewPlants);
        gardenPlantsRecyclerView = view.findViewById(R.id.recyclerViewGardenPlants);
        plants = new ArrayList<>();

        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.api_output);

        sharedPreferencesRecycler();

        return view;
    }

    public void sharedPreferencesRecycler() {
        // Get plant names from SharedPreferences
        SharedPreferencesStorage storage = new SharedPreferencesStorage();

        List<String> plantNames = storage.getPlantNamesFromSharedPreferences(requireContext());

        // Create a list to hold Plant objects
        plants = new ArrayList<>();

        if (plantNames != null) {
            // Create Plant objects with plant names
            for (String plantName : plantNames) {
                Plant plant = new Plant();
                plant.plantName = plantName;
                plants.add(plant);
            }
        }

        // Create and set the adapter
        plantReminderAdapter = new PlantReminderAdapter(requireContext(), plants, this);
        gardenPlantsAdapter = new GardenPlantsAdapter(requireContext(), plants, this);

        // Assuming plantRecyclerView is the RecyclerView in your layout
        plantReminderRecyclerView.setAdapter(plantReminderAdapter);
        gardenPlantsRecyclerView.setAdapter(gardenPlantsAdapter);

        // Set a LinearLayoutManager
        plantReminderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        gardenPlantsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
    }

    @Override
    public void onItemClick(int position, String plantName) {
        PlantDetailsFragment plantDetailsFragment = new PlantDetailsFragment();

        // pass plant name to plant details fragment
        Bundle bundle = new Bundle();
        bundle.putString("plantName", plantName);
        plantDetailsFragment.setArguments(bundle);

        // switch fragments
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.home, plantDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addPlant() {
        CameraFragment cameraFragment = new CameraFragment();

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.home, cameraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Change the selected item in the BottomNavigationView to the camera item
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.camera);
    }
}