package com.example.plantngo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView plantRecyclerView;
    private PlantRecyclerViewAdapter plantAdapter;
    private ArrayList<PlantCard> plantList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        plantRecyclerView = view.findViewById(R.id.recyclerViewPlants);
        plantRecyclerView.setHasFixedSize(true);
//        plantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        plantList = new ArrayList<>();

//        parseJSON();
        return view;
    }


//    // ArrayList for person names, email Id's and mobile numbers
//    ArrayList<String> personNames = new ArrayList<>();
//    ArrayList<String> emailIds = new ArrayList<>();
//    ArrayList<String> mobileNumbers = new ArrayList<>();
//
//
//        // get the reference of RecyclerView
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        // set a LinearLayoutManager with default vertical orientation
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        try {
//            // get JSONObject from JSON file
//            JSONObject obj = new JSONObject(loadJSONFromAsset());
//            // fetch JSONArray named users
//            JSONArray userArray = obj.getJSONArray("users");
//            // implement for loop for getting users list data
//            for (int i = 0; i < userArray.length(); i++) {
//                // create a JSONObject for fetching single user data
//                JSONObject userDetail = userArray.getJSONObject(i);
//                // fetch email and name and store it in arraylist
//                personNames.add(userDetail.getString("name"));
//                emailIds.add(userDetail.getString("email"));
//                // create a object for getting contact data from JSONObject
//                JSONObject contact = userDetail.getJSONObject("contact");
//                // fetch mobile number and store it in arraylist
//                mobileNumbers.add(contact.getString("mobile"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        //  call the constructor of CustomAdapter to send the reference and data to Adapter
//        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, personNames, emailIds, mobileNumbers);
//        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
//    }
//
//    public String loadJSONFromAsset() {
//        String json = null;
//        try {
//            InputStream is = getAssets().open("users_list.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }
}