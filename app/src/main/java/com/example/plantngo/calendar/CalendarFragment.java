package com.example.plantngo.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantngo.R;
import com.example.plantngo.storage.RealtimeDatabaseStorage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for scheduling watering events in the calendar for a specific plant.
 */
public class CalendarFragment extends Fragment {

    private EditText calendarEditText;
    private String stringDateSelected, plantName;
    private DatabaseReference databaseReference;

    /**
     * Default constructor required for the fragment.
     */
    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        TextView plantNameTextView = view.findViewById(R.id.plant_name_textView);
        CalendarView calendar = view.findViewById(R.id.calendar_view);
        ImageView closeButton = view.findViewById(R.id.close_button);
        calendarEditText = view.findViewById(R.id.calendar_editText);
        Button scheduleWateringButton = view.findViewById(R.id.schedule_watering_button);

        RealtimeDatabaseStorage realtimeDatabaseStorage = new RealtimeDatabaseStorage();
        databaseReference = realtimeDatabaseStorage.getDatabaseReference();

        // Retrieve data from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            plantName = bundle.getString("plantName");
            // display plant name in text view
            plantNameTextView.setText(plantName);
        }

        calendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            stringDateSelected = Integer.toString(i) + Integer.toString(i1 + 1) + Integer.toString(i2);
            calendarClicked();
        });

        closeButton.setOnClickListener(view1 -> closeFragment());

        scheduleWateringButton.setOnClickListener(v -> scheduleWatering());

        return view;
    }

    /**
     * Update the UI based on the selected date in the calendar.
     * Retrieves and displays the scheduled event for the selected date.
     */
    private void calendarClicked() {
        databaseReference.child("plants").child(plantName).child("calendar").child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    calendarEditText.setText(snapshot.getValue().toString());
                } else {
                    calendarEditText.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors during data retrieval
            }
        });
    }

    /**
     * Close the current fragment.
     */
    private void closeFragment() {
        getParentFragmentManager().beginTransaction().remove(CalendarFragment.this).commit();
    }

    /**
     * Schedule watering for the selected date.
     */
    private void scheduleWatering() {
        String scheduledEvent = calendarEditText.getText().toString();

        if (stringDateSelected == null) {
            // If empty, set it to today's date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            stringDateSelected = sdf.format(new Date());
        }

        if (!TextUtils.isEmpty(scheduledEvent)) {
            saveScheduledEvent(scheduledEvent);
        } else {
            // Handle the case where the selected date is empty
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the scheduled event to the database.
     *
     * @param scheduledEvent The event to be scheduled.
     */
    private void saveScheduledEvent(String scheduledEvent) {
        databaseReference.child("plants").child(plantName).child("calendar").child(stringDateSelected).setValue(scheduledEvent);
        Toast.makeText(getContext(), "Added to calendar", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update the calendarEditText based on the retrieved data snapshot.
     *
     * @param snapshot Data snapshot containing the scheduled event.
     */
    private void updateCalendarEditText(DataSnapshot snapshot) {
        if (snapshot.getValue() != null) {
            calendarEditText.setText(snapshot.getValue().toString());
        } else {
            calendarEditText.setText(null);
        }
    }
}
