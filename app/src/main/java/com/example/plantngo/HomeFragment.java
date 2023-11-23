package com.example.plantngo;

import android.content.Context;
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

        JsonReader jsonReader = new JsonReader();
        String jsonContent = jsonReader.readJsonFile(getContext(), R.raw.api_output);
        parseJson(jsonContent);

        return view;
    }

    public void parseJson(String jsonContent) {
        JsonReader jsonReader = new JsonReader();
        List<Plant> parsedPlants = jsonReader.parseJson(jsonContent);
        plants.addAll(parsedPlants);
        plantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        plantAdapter = new Adapter(requireContext(),plants);
        plantRecyclerView.setAdapter(plantAdapter);
    }

//    private void extractPlants() {
//        RequestQueue queue = Volley.newRequestQueue(requireContext());
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject plantObject = response.getJSONObject(i);
//
//                        // Get the second object in the "results" array
//                        JSONArray resultsArray = plantObject.getJSONArray("results");
//                        JSONObject speciesResult = resultsArray.getJSONObject(1);
//                        String scientificName = speciesResult.getJSONObject("species")
//                                .getString("scientificNameWithoutAuthor");
//
//                        System.out.println("Scientific Name Without Author: " + scientificName);
//
//                        Plant plant = new Plant();
//                        //plant.setPlantName(plantObject.getString(scientificName));
//                        //plant.setPlantReminders(plantObject.getString("".toString()));
//
//                        // Get the second item in the "query" array
//                        JSONArray queryArray = plantObject.getJSONArray("query");
//                        JSONObject imageResult = queryArray.getJSONObject(1);
//                        JSONObject imageObject = imageResult.getJSONObject("images");
//
//                        //plant.setPlantImageUrl(plantObject.getString(String.valueOf(imageObject)));
//                        plants.add(plant);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                plantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                plantAdapter = new Adapter(getContext(),plants);
//                plantRecyclerView.setAdapter(plantAdapter);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("tag", "onErrorResponse: " + error.getMessage());
//            }
//        });
//        queue.add(jsonArrayRequest);
//    }
}