package com.example.plantngo.plant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantngo.authentication.LoginActivity;
import com.example.plantngo.camera.CameraFragment;
import com.example.plantngo.R;
import com.example.plantngo.storage.SharedPreferencesStorage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying the user's garden plants.
 */
public class HomeFragment extends Fragment implements RecyclerViewInterface {

    // User ID obtained from LoginActivity
    private String userId = LoginActivity.userID;

    // RecyclerView for displaying garden plants
    private RecyclerView gardenPlantsRecyclerView;

    // List to hold Plant objects
    private List<Plant> plants;

    /**
     * Default constructor for HomeFragment.
     */
    public HomeFragment() {
    }

    /**
     * Handles the creation of the view for the HomeFragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // FloatingActionButton for adding a new plant
        FloatingActionButton addPlantButton = view.findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(view1 -> addPlant());

        // RecyclerView for displaying garden plants
        gardenPlantsRecyclerView = view.findViewById(R.id.recyclerViewGardenPlants);
        plants = new ArrayList<>();

        // Load plant names from SharedPreferences and display them in the RecyclerView
        sharedPreferencesRecycler();

        return view;
    }

    /**
     * Retrieves plant names from SharedPreferences and populates the RecyclerView with Plant objects.
     */
    public void sharedPreferencesRecycler() {
        // Get plant names from SharedPreferences
        SharedPreferencesStorage storage = new SharedPreferencesStorage();
        List<String> plantNames = storage.getPlantNamesFromSharedPreferences(requireContext(), userId);

        // Create a list of Plant objects
        plants = new ArrayList<>();

        if (plantNames != null) {
            // Create Plant objects with plant names
            for (String plantName : plantNames) {
                Plant plant = new Plant();
                plant.plantName = plantName;
                plants.add(plant);
            }
        }

        // Create and set the adapter for the RecyclerView
        GardenPlantsAdapter gardenPlantsAdapter = new GardenPlantsAdapter(requireContext(), plants, this);
        gardenPlantsRecyclerView.setAdapter(gardenPlantsAdapter);

        // Set a GridLayoutManager for the RecyclerView
        gardenPlantsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    /**
     * Handles the item click event in the RecyclerView.
     *
     * @param position  The position of the clicked item.
     * @param plantName The name of the clicked plant.
     */
    @Override
    public void onItemClick(int position, String plantName) {
        // Create a new PlantDetailsFragment and pass the selected plant name
        PlantDetailsFragment plantDetailsFragment = new PlantDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("plantName", plantName);
        plantDetailsFragment.setArguments(bundle);

        // Switch to the PlantDetailsFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.home, plantDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Handles the action of adding a new plant.
     */
    public void addPlant() {
        // Create a new CameraFragment for adding a plant
        CameraFragment cameraFragment = new CameraFragment();

        // Switch to the CameraFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.home, cameraFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Change the selected item in the BottomNavigationView to the camera item
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.camera);
    }
}
