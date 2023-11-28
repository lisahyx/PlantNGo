package com.example.plantngo.plant.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.jsonparsing.JsonReader;
import com.example.plantngo.plant.HomeFragment;
import com.example.plantngo.storage.RealtimeDatabaseStorage;
import com.example.plantngo.storage.SharedPreferencesStorage;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

public class PlantDetailsFragment extends Fragment {

    private ImageView openCalendar;
    private TextView plantNameView, sunlightInfo, waterInfo;

    private Button removeButton;
    String plantName;

    private DatabaseReference databaseReference;

    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        plantNameView = view.findViewById(R.id.plantNameView);
        sunlightInfo = view.findViewById(R.id.sunlightInfo);
        waterInfo = view.findViewById(R.id.waterInfo);
        openCalendar = view.findViewById(R.id.calenderImageView);
        removeButton = view.findViewById(R.id.remove_plant_button);

        RealtimeDatabaseStorage realtimeDatabaseStorage = new RealtimeDatabaseStorage();
        databaseReference = realtimeDatabaseStorage.getDatabaseReference();

        // Retrieve data from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            plantName = bundle.getString("plantName");
        }
        if (plantName != null) {
            // display plant name in text view
            plantNameView.setText(plantName);
        }

        getSunlight();
        getWateringInfo();

        openCalendar.setOnClickListener(view1 -> {
            CalendarFragment calendarFragment = new CalendarFragment();

            // pass plant name to calendar fragment
            Bundle bundle2 = new Bundle();
            bundle2.putString("plantName", plantName);
            calendarFragment.setArguments(bundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.plant_details, calendarFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        removeButton.setOnClickListener(view12 -> {
            removePlantFromGarden(plantName);
        });

        return view;
    }

    public void getSunlight() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_care_api_output);
        if (jsonContent != null) {
            try {
                String sunlight = jsonReader.parseSunlightJson(jsonContent);

                if (sunlight != null) {
                    databaseReference.child("plants").child(plantName).child("plantCare").child("Sunlight").setValue(sunlight);
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
                    databaseReference.child("plants").child(plantName).child("plantCare").child("Water").setValue(wateringInfo);
                    waterInfo.setText(wateringInfo);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removePlantFromGarden (String plantName) {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Set the message show for the Alert time
        builder.setMessage("Remove " + plantName + " from Garden?");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            SharedPreferencesStorage sharedPreferencesStorage = new SharedPreferencesStorage();
            sharedPreferencesStorage.deletePlantNameFromSharedPreferences(requireContext(), plantName);
            Toast.makeText(getContext(),"Plant removed from garden", Toast.LENGTH_SHORT).show();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            HomeFragment homeFragment = new HomeFragment();
            transaction.replace(R.id.plant_details, homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Toast.makeText(requireContext(),"Please refresh page", Toast.LENGTH_SHORT).show();

            // Close the current fragment
            getParentFragmentManager().beginTransaction().remove(PlantDetailsFragment.this).commit();

        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}