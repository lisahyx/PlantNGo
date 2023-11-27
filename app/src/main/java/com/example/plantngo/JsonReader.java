package com.example.plantngo;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public String parsePlantNameJson(String jsonContent) throws JSONException {
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

    public String parseSunlightJson(String jsonContent) throws JSONException {
        String sunlight = null;

        if (jsonContent != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);

                if (jsonObject.has("sunlight")) {
                    JSONArray sunlightArray = jsonObject.getJSONArray("sunlight");

                    if (sunlightArray.length() > 0) {
                        sunlight = sunlightArray.getString(0);
                        return sunlight;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sunlight;
    }

}

