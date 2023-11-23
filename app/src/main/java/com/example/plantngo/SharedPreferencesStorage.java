package com.example.plantngo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesStorage {

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
    public void addPlantNameToSharedPreferences(Context context, String newPlantName) {
        List<String> existingPlantNames = getPlantNamesFromSharedPreferences(context);

        if (existingPlantNames == null) {
            existingPlantNames = new ArrayList<>();
        }

        existingPlantNames.add(newPlantName);
        savePlantNamesToSharedPreferences(context, existingPlantNames);
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
}
