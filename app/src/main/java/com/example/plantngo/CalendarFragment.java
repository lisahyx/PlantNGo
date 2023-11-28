package com.example.plantngo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private ImageView closeButton;
    private EditText calenderEditText;
    private Button scheduleWateringButton;
    private String stringDateSelected, plantName;
    private TextView plantNameView;
    private DatabaseReference databaseReference;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();
    String userId = fUser.getUid();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        plantNameView = view.findViewById(R.id.plantNameView);
        calendarView = view.findViewById(R.id.calendarView);
        closeButton = view.findViewById(R.id.close);
        calenderEditText = view.findViewById(R.id.calenderEditText);
        scheduleWateringButton = view.findViewById(R.id.scheduleWateringButton);
        databaseReference = FirebaseDatabase.
                getInstance("https://plantngo-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child("userID: " + userId);

        // Retrieve data from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            plantName = bundle.getString("plantName");
            // display plant name in text view
            plantNameView.setText(plantName);
        }

        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            stringDateSelected = Integer.toString(i) + Integer.toString(i1+1) + Integer.toString(i2);
            calendarClicked();
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlantDetailsFragment plantDetailsFragment = new PlantDetailsFragment();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.calendar_fragment, plantDetailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        scheduleWateringButton.setOnClickListener(v -> {
            String scheduledEvent = calenderEditText.getText().toString();

            if(stringDateSelected == null) {
                // If empty, set it to today's date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                stringDateSelected = sdf.format(new Date());
            }

            if (!TextUtils.isEmpty(scheduledEvent)) {
                databaseReference.child("calendar").child(plantName).child(stringDateSelected).setValue(scheduledEvent);
                Toast.makeText(getContext(), "Added to calendar", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where the selected date is empty
                Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void calendarClicked(){
        databaseReference.child("calendar").child(plantName).child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    calenderEditText.setText(snapshot.getValue().toString());
                }else {
                    calenderEditText.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}