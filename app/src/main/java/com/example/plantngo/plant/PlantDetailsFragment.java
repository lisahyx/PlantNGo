package com.example.plantngo.plant;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.authentication.LoginActivity;
import com.example.plantngo.calendar.CalendarFragment;
import com.example.plantngo.notification.NotificationReceiver;
import com.example.plantngo.storage.JsonReader;
import com.example.plantngo.storage.RealtimeDatabaseStorage;
import com.example.plantngo.storage.SharedPreferencesStorage;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

/**
 * Fragment to display details of a plant in the garden.
 */
public class PlantDetailsFragment extends Fragment {
    private TextView sunlightInfoTextView;
    private TextView waterInfoTextView;
    private ImageButton notificationButton;
    private String plantName;
    private boolean isNotificationActive;

    private DatabaseReference databaseReference;

    String userId = LoginActivity.userID;

    /**
     * Default constructor for PlantDetailsFragment.
     */
    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);

        TextView plantNameView = view.findViewById(R.id.plant_name_textView);
        sunlightInfoTextView = view.findViewById(R.id.sunlight_info_textView);
        waterInfoTextView = view.findViewById(R.id.water_info_textView);
        ImageView calendarImageView = view.findViewById(R.id.calendar_imageView);
        Button removeButton = view.findViewById(R.id.remove_plant_button);
        notificationButton = view.findViewById(R.id.notification_button);

        NotificationReceiver notificationReceiver = new NotificationReceiver();

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

        getSunlightInfo();
        getWateringInfo();

        setNotificationButtonImage(notificationReceiver);

        notificationButton.setOnClickListener(v -> {
            setNotification(notificationReceiver);
        });

        calendarImageView.setOnClickListener(v -> {
            openCalender(bundle);
        });

        removeButton.setOnClickListener(v -> {
            removePlantFromGarden(plantName);
        });

        return view;
    }

    public void openCalender(Bundle bundle) {
        CalendarFragment calendarFragment = new CalendarFragment();

        // pass plant name to calendar fragment
        Bundle bundle2 = new Bundle();
        bundle2.putString("plantName", plantName);
        calendarFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.plant_details, calendarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Get sunlight information from the plant care API and update the UI.
     */
    public void getSunlightInfo() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_care_api_output);

        if (jsonContent != null) {
            try {
                String sunlight = jsonReader.parseSunlightJson(jsonContent);

                if (sunlight != null) {
                    databaseReference.child("plants").child(plantName).child("plantCare").child("Sunlight").setValue(sunlight);
                    sunlightInfoTextView.setText(sunlight);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get watering information from the plant care API and update the UI.
     */
    public void getWateringInfo() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.plant_care_api_output);

        if (jsonContent != null) {
            try {
                String wateringInfo = jsonReader.parseWateringJson(jsonContent);

                if (wateringInfo != null) {
                    databaseReference.child("plants").child(plantName).child("plantCare").child("Water").setValue(wateringInfo);
                    waterInfoTextView.setText(wateringInfo);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Set the image resource for the notification button based on the current notification state.
     *
     * @param notificationReceiver The NotificationReceiver instance used to manage notifications.
     */
    public void setNotificationButtonImage(NotificationReceiver notificationReceiver) {
        // Get the current notification state for the plant
        isNotificationActive = notificationReceiver.getNotificationState(requireContext(), plantName);

        // Update the image resource based on the state
        int imageResource = isNotificationActive ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_notifications_24;
        notificationButton.setImageResource(imageResource);
    }

    /**
     * Toggle the notification state, update the UI, and schedule or cancel the notification.
     *
     * @param notificationReceiver The NotificationReceiver instance used to manage notifications.
     */
    public void setNotification(NotificationReceiver notificationReceiver) {
        // Get the current notification time
        Long time = JsonReader.getNotificationTime();

        // Get the current notification state for the plant
        isNotificationActive = notificationReceiver.getNotificationState(requireContext(), plantName);

        // Toggle the notification state
        isNotificationActive = !isNotificationActive;

        // Update the image resource based on the state
        int imageResource = isNotificationActive ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_notifications_24;
        notificationButton.setImageResource(imageResource);

        // Save the updated notification state to SharedPreferences
        notificationReceiver.saveNotificationState(requireContext(), plantName, isNotificationActive);

        // Schedule or cancel the notification based on the state
        if (isNotificationActive) {
            notificationReceiver.scheduleNotification(requireContext(), plantName, notificationReceiver.getNotification(requireContext(), plantName), time);
        } else {
            // Cancel the notification by passing the same ID used when scheduling
            notificationReceiver.cancelScheduledNotification(requireContext(), plantName);
        }

        // List the scheduled notifications for debugging purposes
        notificationReceiver.listScheduledNotifications(requireContext());
    }


    /**
     * Remove a plant from the garden, displaying a confirmation dialog.
     *
     * @param plantName The name of the plant to be removed.
     */
    public void removePlantFromGarden (String plantName) {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Set the message show for the Alert time
        builder.setMessage("Remove " + plantName + " from Garden?");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            SharedPreferencesStorage sharedPreferencesStorage = new SharedPreferencesStorage();
            sharedPreferencesStorage.deletePlantNameFromSharedPreferences(requireContext(), userId, plantName);
            Toast.makeText(getContext(),"Plant removed from garden", Toast.LENGTH_SHORT).show();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.plant_details, new HomeFragment());
            transaction.commit();
            Toast.makeText(requireContext(),"Please refresh page", Toast.LENGTH_SHORT).show();

            // Close the current fragment
            getParentFragmentManager().beginTransaction().remove(PlantDetailsFragment.this).commit();

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}