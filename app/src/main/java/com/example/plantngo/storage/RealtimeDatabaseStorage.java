package com.example.plantngo.storage;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RealtimeDatabaseStorage {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();
    String userId = fUser.getUid();
    private DatabaseReference databaseReference = FirebaseDatabase.
            getInstance("https://plantngo-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child("userID: " + userId);;

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

    public void deletePlantNameFromRealtimeDatabase(String plantName) {
        // Remove the plant name node from the Realtime Database
        databaseReference.child("plants").child(plantName).removeValue()
                .addOnSuccessListener(aVoid ->
                        Log.d("RealtimeDatabase", "Plant name deleted successfully"))
                .addOnFailureListener(e ->
                        Log.w("RealtimeDatabase", "Error deleting plant name", e));
    }

    public String getCurrentDate() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Format the current date (optional)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    public DatabaseReference getDatabaseReference() {
        return this.databaseReference;
    }
}
