package com.example.plantngo.storage;

import android.util.Log;

import com.example.plantngo.authentication.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The RealtimeDatabaseStorage class provides methods to interact with Firebase Realtime Database.
 */
public class RealtimeDatabaseStorage {

    // Firebase authentication instance
    private String userId = LoginActivity.userID;

    // Reference to the Firebase Realtime Database
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://plantngo-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child("userID: " + userId);

    /**
     * Saves a list of plant names to the Realtime Database along with the current date.
     *
     * @param plantNames The list of plant names to be saved.
     */
    public void savePlantNamesToRealtimeDatabase(List<String> plantNames) {
        // Save each plant name individually
        for (String plantName : plantNames) {
            databaseReference.child("plants").child(plantName).child("dateAdded").setValue(getCurrentDate())
                    .addOnSuccessListener(aVoid ->
                            Log.d("RealtimeDatabase", "Plant name added successfully: " + plantName))
                    .addOnFailureListener(e ->
                            Log.w("RealtimeDatabase", "Error adding plant name: " + plantName, e));
        }
    }

    /**
     * Deletes a plant name from the Realtime Database.
     *
     * @param plantName The name of the plant to be deleted.
     */
    public void deletePlantNameFromRealtimeDatabase(String plantName) {
        // Remove the plant name node from the Realtime Database
        databaseReference.child("plants").child(plantName).removeValue()
                .addOnSuccessListener(aVoid ->
                        Log.d("RealtimeDatabase", "Plant name deleted successfully"))
                .addOnFailureListener(e ->
                        Log.w("RealtimeDatabase", "Error deleting plant name", e));
    }

    /**
     * Gets the current date in the "yyyy-MM-dd" format.
     *
     * @return The current date.
     */
    public String getCurrentDate() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Format the current date (optional)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    /**
     * Gets the DatabaseReference instance for further database interactions.
     *
     * @return The DatabaseReference instance.
     */
    public DatabaseReference getDatabaseReference() {
        return this.databaseReference;
    }
}
