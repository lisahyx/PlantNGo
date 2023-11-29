package com.example.plantngo.plant.detail;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantngo.NotificationReceiver;
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

    private ImageButton notificationButton;
    String plantName;
    Long time;

    private DatabaseReference databaseReference;

    NotificationReceiver notificationReceiver = new NotificationReceiver();

    public static String NOTIFICATION_CHANEL_ID = "1001 ";
    public static String default_notification_id = "default";

    private boolean isNotificationActive = false;

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
        notificationButton = view.findViewById(R.id.notification_button);

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

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long time = JsonReader.getNotificationTime();

                // Toggle the notification state
                isNotificationActive = !isNotificationActive;

                // Update the image resource based on the state
                int imageResource = isNotificationActive ? R.drawable.baseline_notifications_active_24 : R.drawable.baseline_notifications_24;
                notificationButton.setImageResource(imageResource);

                // Schedule or cancel the notification based on the state
                if (isNotificationActive) {
                    scheduleNotification(getNotification(plantName), time);
                } else {
                    // Cancel the notification by passing the same ID used when scheduling
                    cancelScheduledNotification(plantName);
                }
            }
        });

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

    public void scheduleNotification(Notification notification, long delay) {
        int notificationId = plantName.hashCode();

        Intent notificationIntent = new Intent(requireContext(), NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATIONID, notificationId);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION,notification);
        notificationIntent.putExtra("plantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureMillis = SystemClock.elapsedRealtime()+delay;
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager!=null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureMillis, pendingIntent);

        // Log information about the scheduled notification
        Log.d("ScheduledNotification", "ID: " + plantName.hashCode());
        Log.d("ScheduledNotification", "Plant: " + plantName);
        Log.d("ScheduledNotification", "Scheduled Time: " + futureMillis);
        Log.d("SchedulingStatus", "Notification scheduled");

        Toast.makeText(requireContext(), "Watering reminder notification scheduled for " + plantName, Toast.LENGTH_SHORT).show();
    }

    public void cancelScheduledNotification(String plantName) {
        int notificationId = plantName.hashCode();

        // Create the PendingIntent with the same details
        Intent notificationIntent = new Intent(requireContext(), NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATIONID, notificationId);
        notificationIntent.putExtra("plantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Cancel the PendingIntent
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);

        // Log information about the canceled notification
        Log.d("CanceledNotification", "ID: " + notificationId);
        Log.d("CanceledNotification", "Plant: " + plantName);
        Log.d("CancellationStatus", "Notification canceled");

        Toast.makeText(requireContext(), "Watering reminder notification for " + plantName + " is cancelled", Toast.LENGTH_SHORT).show();
    }


    public Notification getNotification(String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), default_notification_id);

        builder.setContentTitle("Watering Reminder");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANEL_ID);

        return builder.build();
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

                    time = jsonReader.getNotificationTime();

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