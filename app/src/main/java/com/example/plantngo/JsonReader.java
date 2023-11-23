package com.example.plantngo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonReader {

    public String readJsonFile(Context context, int resourceId) {
        try {
            // Open the resource using getResourceAsStream
            InputStream inputStream = context.getResources().openRawResource(resourceId);

            // Read the InputStream using a BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Close the InputStream and BufferedReader
            reader.close();
            inputStream.close();

            // Return the content of the file as a string
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Plant> parseJson(Context context, String jsonContent) {
        List<Plant> plants = new ArrayList<>();

        if (jsonContent != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonContent);

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

                                    Plant plant = new Plant();
                                    plant.plantName = scientificName;
                                    plants.add(plant);
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
        return plants;
    }

    private void writeJsonToFile(String filePath, String jsonContent) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(jsonContent.getBytes());
            Log.d("JsonWriting", "JSON written to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generalParse(String jsonContent) throws JSONException {
        String scientificName = null;

        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);

                if (jsonObject.has("results")) {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    if (resultsArray.length() > 0) {
                        JSONObject firstResultObject = resultsArray.getJSONObject(0);

                        if (firstResultObject.has("species")) {
                            JSONObject speciesObject = firstResultObject.getJSONObject("species");

                            if (speciesObject.has("scientificNameWithoutAuthor")) {
                                scientificName = speciesObject.getString("scientificNameWithoutAuthor");
                                return scientificName;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return scientificName;
    }
}

