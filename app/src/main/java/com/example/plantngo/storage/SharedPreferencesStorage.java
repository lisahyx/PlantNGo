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

/**
 * The SharedPreferencesStorage class provides methods to interact with SharedPreferences for plant data storage.
 */
public class SharedPreferencesStorage {

    // Instance of RealtimeDatabaseStorage for additional interactions with the Realtime Database
    RealtimeDatabaseStorage realtimeDatabase = new RealtimeDatabaseStorage();

    /**
     * Saves a list of plant names to SharedPreferences for a specific user.
     *
     * @param context    The application context.
     * @param userId     The unique identifier of the user.
     * @param plantNames The list of plant names to be saved.
     */
    public void savePlantNamesToSharedPreferences(Context context, String userId, List<String> plantNames) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlantPreferences_" + userId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the list to a JSON array string
        String plantNamesJson = new Gson().toJson(plantNames);

        editor.putString("PlantNames", plantNamesJson);
        editor.apply();
    }

    /**
     * Retrieves a list of plant names from SharedPreferences for a specific user.
     *
     * @param context The application context.
     * @param userId  The unique identifier of the user.
     * @return The list of plant names retrieved from SharedPreferences.
     */
    public List<String> getPlantNamesFromSharedPreferences(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlantPreferences_" + userId, Context.MODE_PRIVATE);

        // Retrieve the JSON array string
        String plantNamesJson = sharedPreferences.getString("PlantNames", "");

        // Convert the JSON array string back to a list
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(plantNamesJson, listType);
    }

    /**
     * Adds a new plant name to the existing list in SharedPreferences for a specific user.
     *
     * @param context   The application context.
     * @param userId    The unique identifier of the user.
     * @param plantName The name of the plant to be added.
     */
    public void addPlantNameToSharedPreferences(Context context, String userId, String plantName) {
        // Save plant name
        SharedPreferencesStorage storage = new SharedPreferencesStorage();

        // Retrieve existing plant names
        List<String> retrievedPlantNames = storage.getPlantNamesFromSharedPreferences(context, userId);

        // Check for duplicates
        if (retrievedPlantNames != null && retrievedPlantNames.contains(plantName)) {
            Toast.makeText(context, "Plant name already exists: " + plantName, Toast.LENGTH_SHORT).show();
        }
        // Add the new plant name to the existing list
        else if (retrievedPlantNames == null) {
            retrievedPlantNames = new ArrayList<>();
            retrievedPlantNames.add(plantName);
            Toast.makeText(context, "Plant Added to Garden", Toast.LENGTH_SHORT).show();
        } else {
            retrievedPlantNames.add(plantName);
            Toast.makeText(context, "Plant Added to Garden", Toast.LENGTH_SHORT).show();
        }

        // Save the updated list to SharedPreferences
        storage.savePlantNamesToSharedPreferences(context, userId, retrievedPlantNames);

        // Save the updated list to Realtime Database
        realtimeDatabase.savePlantNamesToRealtimeDatabase(retrievedPlantNames);

        // Log all plant names
        storage.displayAllPlantNames(context, userId);
    }

    /**
     * Displays all plant names stored in SharedPreferences for a specific user.
     *
     * @param context The application context.
     * @param userId  The unique identifier of the user.
     */
    public void displayAllPlantNames(Context context, String userId) {
        List<String> plantNames = getPlantNamesFromSharedPreferences(context, userId);

        if (plantNames != null) {
            for (String plantName : plantNames) {
                Log.d("PlantName", plantName);
            }
        }
    }

    /**
     * Deletes a plant name from SharedPreferences for a specific user.
     *
     * @param context   The application context.
     * @param userId    The unique identifier of the user.
     * @param plantName The name of the plant to be deleted.
     */
    public void deletePlantNameFromSharedPreferences(Context context, String userId, String plantName) {
        // Retrieve the existing list of plant names
        List<String> plantNames = getPlantNamesFromSharedPreferences(context, userId);

        if (plantNames != null) {
            // Remove the specified plant name
            plantNames.remove(plantName);

            // Save the updated list to SharedPreferences
            savePlantNamesToSharedPreferences(context, userId, plantNames);

            // Delete the plant name from the Realtime Database
            realtimeDatabase.deletePlantNameFromRealtimeDatabase(plantName);

            // Log all plant names after deletion
            displayAllPlantNames(context, userId);
        }
    }
}
