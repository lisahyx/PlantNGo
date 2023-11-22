package com.example.plantngo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import retrofit2.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView plantRecyclerView;
    private Adapter plantAdapter;
    private List<Plant> plants;
    private static String JSON_URL = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        plantRecyclerView = view.findViewById(R.id.recyclerViewPlants);
        plants = new ArrayList<>();

        //extractPlants();
        parseLocalJson();

        return view;
    }

    private void parseLocalJson() {
        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.api_output);
        Plant plant = new Plant();

        if (jsonContent != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonContent);
                //JSONObject jsonObject= new JSONObject(jsonContent);

                if (jsonArray.length() > 0) {
                    JSONObject firstResult = jsonArray.getJSONObject(0);

                    if (firstResult.has("results")) {
                        JSONArray resultsArray = firstResult.getJSONArray("results");

                        if (resultsArray.length() > 0) {
                            JSONObject firstResultObject = resultsArray.getJSONObject(0);

                            if (firstResultObject.has("species")) {
                                JSONObject speciesObject = firstResultObject.getJSONObject("species");

                                if (speciesObject.has("scientificNameWithoutAuthor")) {
                                    String scientificName = speciesObject.getString("scientificNameWithoutAuthor");

                                    Log.d("JsonParsing", "Scientific Name Without Author: " + scientificName);

                                    //plant.setPlantName(scientificName);
                                    plant.plantName = scientificName;
                                } else {
                                    Log.d("JsonParsing", "No value for scientificNameWithoutAuthor in species");
                                }
                            } else {
                                Log.d("JsonParsing", "No 'species' key in the result object");
                            }
                        } else {
                            Log.d("JsonParsing", "No results in the array");
                        }
                    } else {
                        Log.d("JsonParsing", "No 'results' key in the first object");
                    }
                } else {
                    Log.d("JsonParsing", "Not enough objects in the array");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        plants.add(plant);
        plantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        plantAdapter = new Adapter(requireContext(),plants);
        plantRecyclerView.setAdapter(plantAdapter);
    }




    private void extractPlants() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject plantObject = response.getJSONObject(i);

                        // Get the second object in the "results" array
                        JSONArray resultsArray = plantObject.getJSONArray("results");
                        JSONObject speciesResult = resultsArray.getJSONObject(1);
                        String scientificName = speciesResult.getJSONObject("species")
                                .getString("scientificNameWithoutAuthor");

                        System.out.println("Scientific Name Without Author: " + scientificName);

                        Plant plant = new Plant();
                        //plant.setPlantName(plantObject.getString(scientificName));
                        //plant.setPlantReminders(plantObject.getString("".toString()));

                        // Get the second item in the "query" array
                        JSONArray queryArray = plantObject.getJSONArray("query");
                        JSONObject imageResult = queryArray.getJSONObject(1);
                        JSONObject imageObject = imageResult.getJSONObject("images");

                        //plant.setPlantImageUrl(plantObject.getString(String.valueOf(imageObject)));
                        plants.add(plant);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                plantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                plantAdapter = new Adapter(getContext(),plants);
                plantRecyclerView.setAdapter(plantAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(jsonArrayRequest);

    }


//    private void parseJSON() {
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("hits");
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject hit = jsonArray.getJSONObject(i);
//
//                                String plantName = hit.getString("plant");
//                                String imageUrl = hit.getString("image");
//
//                                plantList.add(new PlantCard(imageUrl, plantName));
//                            }
//
//                            plantAdapter = new PlantRecyclerViewAdapter(getContext(), plantList);
//                            plantRecyclerView.setAdapter(plantAdapter);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//    }


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