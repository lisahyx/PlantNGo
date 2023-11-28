package com.example.plantngo.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesStorage {

    RealtimeDatabaseStorage realtimeDatabase = new RealtimeDatabaseStorage();

    // Save plant names to SharedPreferences
    public void savePlantNamesToSharedPreferences(Context context, List<String> plantNames) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlantPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the list to a JSON array string
        String plantNamesJson = new Gson().toJson(plantNames);

        editor.putString("PlantNames", plantNamesJson);
        editor.apply();
    }

    // Retrieve plant names from SharedPreferences
    public List<String> getPlantNamesFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlantPreferences", Context.MODE_PRIVATE);

        // Retrieve the JSON array string
        String plantNamesJson = sharedPreferences.getString("PlantNames", "");

        // Convert the JSON array string back to a list
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(plantNamesJson, listType);
    }

    // Add a new plant name to the existing list in SharedPreferences
    public void addPlantNameToSharedPreferences(Context context, String plantName) {
        // Save plant name
        SharedPreferencesStorage storage = new SharedPreferencesStorage();

        // Retrieve existing plant names
        List<String> retrievedPlantNames = storage.getPlantNamesFromSharedPreferences(context);

        // Check for duplicates
        if (retrievedPlantNames != null && retrievedPlantNames.contains(plantName)) {
            Toast.makeText(context, "Plant name already exists: " + plantName, Toast.LENGTH_SHORT).show();
        }
        // Add the new plant name to the existing list
        else if (retrievedPlantNames == null) {
            retrievedPlantNames = new ArrayList<>();
            retrievedPlantNames.add(plantName);
            Toast.makeText(context, "Plant Added to Garden", Toast.LENGTH_SHORT).show();
        } else if (retrievedPlantNames != null) {
            retrievedPlantNames.add(plantName);
            Toast.makeText(context, "Plant Added to Garden", Toast.LENGTH_SHORT).show();
        }

        // Save the updated list to SharedPreferences
        storage.savePlantNamesToSharedPreferences(context, retrievedPlantNames);

        realtimeDatabase.savePlantNamesToRealtimeDatabase(retrievedPlantNames);

        // Log all plant names
        storage.displayAllPlantNames(context);
    }

    // Display all plant names stored in SharedPreferences
    public void displayAllPlantNames(Context context) {
        List<String> plantNames = getPlantNamesFromSharedPreferences(context);

        if (plantNames != null) {
            for (String plantName : plantNames) {
                Log.d("PlantName", plantName);
            }
        }
    }

    // Delete a plant name from SharedPreferences
    public void deletePlantNameFromSharedPreferences(Context context, String plantName) {
        // Retrieve the existing list of plant names
        List<String> plantNames = getPlantNamesFromSharedPreferences(context);

        if (plantNames != null) {
            // Remove the specified plant name
            plantNames.remove(plantName);

            // Save the updated list to SharedPreferences
            savePlantNamesToSharedPreferences(context, plantNames);

            realtimeDatabase.deletePlantNameFromRealtimeDatabase(plantName);

            // Log all plant names after deletion
            displayAllPlantNames(context);
        }
    }
}
